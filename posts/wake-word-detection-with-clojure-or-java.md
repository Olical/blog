# Wake word detection with Clojure (or Java)

> Hello there, intrepid Clojurian.
> If you don’t want to follow along with this post and just want to copy some code, go to [Olical/clojure-wake-word-detection](https://github.com/Olical/clojure-wake-word-detection).
> Enjoy!

If you follow me on [twitter](https://twitter.com/OliverCaldwell) or [GitHub](https://github.com/Olical/) you’ve probably seen me working on [Snowball](https://github.com/Olical/snowball), a voice activated [Discord](https://discordapp.com/) bot written in [Clojure](https://clojure.org/).

It works fairly well and allows my friends and I to move people around channels and control our music bot over voice while we’re in a game.
For example:

> O: Hey Snowball
>
> S: Hey, Olical
>
> O: Pause the music
>
> S: Sure thing

The voice control has a few steps to it:

1. Receive all audio from a Discord voice channel and store per-user streams of audio data.
2. When someone says something and then stops speaking we need to check that audio for the wake phrase "hey snowball".
3. If we spot the wake phrase we then need to wait for them to say another sentence which we’ll treat as a command.
4. Once we’ve got that second phrase it can be sent off to Google Cloud Platform (in my case) for speech recognition.
5. The program can then act on that command.

Sending audio off to Google from Clojure or Java is actually pretty straightforward and well documented, you can also check out [snowball.comprehension](https://github.com/Olical/snowball/blob/b8be304046e98c2c082fa6f583ebf9950bf412ac/src/clojure/snowball/comprehension.clj#L139-L172) to see how I managed it.

The part that didn’t seem to exist was the wake word detection, I had to write a little C and some Java to get it working.
Here’s what I ended up with so you can use it in your projects.

## Tooling

I hunted high and low for a JVM compatible wake word detector, I tried to use [cmusphinx](https://cmusphinx.github.io/) for longer than I’d like to admit and couldn’t get anything working.
The one time I had it processing audio it was wildly inaccurate and hogging my CPU, I was probably doing something massively wrong.

I finally stumbled across [Porcupine](https://github.com/Picovoice/Porcupine) and can highly recommend it, it’s specialised for this job and works great.
The only problem was it didn’t have any support for JVM on the desktop, there were some Android bindings but that’s all.
I wrote a little C and some Java so my Clojure could talk to Porcupine.

Before I get to that though, let’s set up a little `Makefile` that will fetch Porcupine (watch out, the repository is around 3GB) and configure a wake phrase.

```makefile
WAKE_PHRASE := hey porcupine

wake-word-engine: wake-word-engine/Porcupine wake-word-engine/wake_phrase.ppn

wake-word-engine/Porcupine:
	mkdir -p wake-word-engine
	cd wake-word-engine && git clone git@github.com:Picovoice/Porcupine.git

wake-word-engine/wake_phrase.ppn: wake-word-engine/Porcupine
	cd wake-word-engine/Porcupine && tools/optimizer/linux/x86_64/pv_porcupine_optimizer -r resources/ -w "$(WAKE_PHRASE)" -p linux -o ../
	mv "wake-word-engine/$(WAKE_PHRASE)_linux.ppn" wake-word-engine/wake_phrase.ppn
```

When we run `make` it’ll download the Porcupine repo (if we don’t have it already) and build the `.ppn` file.
We use this file to configure Porcupine at runtime with the desired wake phrase.
Run this so that we have Porcupine downloaded and ready to use.

## Bindings

We have Porcupine but we have no way for the JVM to talk to it, it’s a native library written in closed source C, luckily they expose enough through shared libraries that we can easily write our own bindings.
You may want to extend the following Java and C yourself, but this was enough for me.

> I’ve chosen `wakeup` as a root namespace but obviously you could use something else.
> In Snowball I have `snowball.porcupine` instead of `wakeup.porcupine`, I just thought I’d make it generic for this post.

Place this in `src/java/wakeup/porcupine/Porcupine.java`:

```java
// Copied and modified from the Porcupine project Android binding.
// https://github.com/Picovoice/Porcupine

package wakeup.porcupine;

public class Porcupine {
    private final long object;

    static {
        System.loadLibrary("pv_porcupine");
    }

    public Porcupine(String modelFilePath, String keywordFilePath, float sens) throws Exception {
        try {
            object = init(modelFilePath, keywordFilePath, sens);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public boolean processFrame(short[] pcm) throws Exception {
        try {
            return process(object, pcm);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public void delete() {
        delete(object);
    }

    public native int getFrameLength();

    public native int getSampleRate();

    private native long init(String modelFilePath, String keywordFilePaths, float sensitivitie);

    private native boolean process(long object, short[] pcm);

    private native void delete(long object);
}
```

Place this in `src/c/porcupine.c`:

```c
#include <stdio.h>
#include <pv_porcupine.h>
#include <wakeup_porcupine_Porcupine.h>

JNIEXPORT jlong JNICALL Java_wakeup_porcupine_Porcupine_init
  (JNIEnv *env, jobject obj, jstring model_raw, jstring keyword_raw, jfloat sens) {
   const char *model = (*env)->GetStringUTFChars(env, model_raw, 0);
   const char *keyword = (*env)->GetStringUTFChars(env, keyword_raw, 0);
   pv_porcupine_object_t *handle;

   const pv_status_t status = pv_porcupine_init(model, keyword, sens, &handle);

   if (status != PV_STATUS_SUCCESS) {
       printf("Error: Failed to initialise the Porcupine instance.");
   }

   (*env)->ReleaseStringUTFChars(env, model_raw, model);
   (*env)->ReleaseStringUTFChars(env, keyword_raw, keyword);

   return (long)handle;
}

JNIEXPORT void JNICALL Java_wakeup_porcupine_Porcupine_delete
  (JNIEnv *env, jobject obj, jlong handle) {
  pv_porcupine_delete((pv_porcupine_object_t*)handle);
}

JNIEXPORT jint JNICALL Java_wakeup_porcupine_Porcupine_getFrameLength
  (JNIEnv *env, jobject obj) {
  return pv_porcupine_frame_length();
}

JNIEXPORT jint JNICALL Java_wakeup_porcupine_Porcupine_getSampleRate
  (JNIEnv *env, jobject obj) {
  return pv_sample_rate();
}

JNIEXPORT jboolean JNICALL Java_wakeup_porcupine_Porcupine_process
  (JNIEnv *env, jobject obj, jlong handle, jshortArray pcm_raw) {
  jshort *pcm = (*env)->GetShortArrayElements(env, pcm_raw, 0);
  bool result;

  pv_porcupine_process((pv_porcupine_object_t*)handle, pcm, &result);

  (*env)->ReleaseShortArrayElements(env, pcm_raw, pcm, 0);

  return result;
}
```

Now let’s modify the `Makefile` to compile our C and Java code:

```makefile
WAKE_PHRASE := hey porcupine

wake-word-engine: wake-word-engine/Porcupine wake-word-engine/wake_phrase.ppn wake-word-engine/jni/libpv_porcupine.so src/java/wakeup/porcupine/Porcupine.class

# ------- 8< -------

src/java/wakeup/porcupine/Porcupine.class wake-word-engine/jni/wakeup_porcupine_Porcupine.h: src/java/wakeup/porcupine/Porcupine.java
	mkdir -p wake-word-engine/jni
	javac -h wake-word-engine/jni src/java/wakeup/porcupine/Porcupine.java

wake-word-engine/jni/libpv_porcupine.so: wake-word-engine/jni/wakeup_porcupine_Porcupine.h src/c/porcupine.c
	gcc -shared -O3 \
		-I/usr/include \
		-I/usr/lib/jvm/default/include \
		-I/usr/lib/jvm/default/include/linux \
		-Iwake-word-engine/Porcupine/include \
		-Iwake-word-engine/jni \
		src/c/porcupine.c \
		wake-word-engine/Porcupine/lib/linux/x86_64/libpv_porcupine.a \
		-o wake-word-engine/jni/libpv_porcupine.so
```

So the default `make` command, `wake-word-engine`, now depends upon Porcupine, the `.ppn` file, C compiled to a `.so` and Java compiled to a `.class`.
We can now write some Clojure that imports `[wakeup.porcupine Porcupine]` and runs some audio through it.

## Using the binding

I’ve written a Clojure namespace that grabs your microphone as input and streams it through the Porcupine binding, logging whenever it hears the wake phrase.
I think this should be more than enough to get most people going with their own voice activated programs.

Place this in `src/clojure/wakeup/main.clj`:

```clojure
(ns wakeup.main
  (:import [wakeup.porcupine Porcupine]

           ;; These are required for the microphone input.
           [javax.sound.sampled AudioFormat DataLine TargetDataLine AudioSystem]))

;; Notes on audio formats:
;; Discord provides audio as `48KHz 16bit stereo signed BigEndian PCM`.
;; Porcupine requires `16KHz 16bit mono signed LittleEndian PCM` but in 512 length short-array frames (a short is two bytes).
;; GCP speech recognition requires the same as Porcupine but as byte pairs and without the 512 frames.

(defn init-porcupine []
  (Porcupine. "wake-word-engine/Porcupine/lib/common/porcupine_params.pv"
              "wake-word-engine/wake_phrase.ppn"
              0.5))

;; Adapted from: https://gist.github.com/BurkeB/ebf5f01c0d20ff6b9dc111ac427ddea8
(defn with-microphone [f]
  (let [audio-format (new AudioFormat 16000 16 1 true true)
        info (new javax.sound.sampled.DataLine$Info TargetDataLine audio-format)]

    (when-not (AudioSystem/isLineSupported info)
      (throw (Error. "AudioSystem/isLineSupported returned false")))

    (with-open [line (AudioSystem/getTargetDataLine audio-format)]
      (doto line
        (.open audio-format)
        (.start))

      (f line))))

(defn byte-pair->short [[a b]]
  (bit-or (bit-shift-left a 8) (bit-and b 0xFF)))

(defn bytes->shorts [buf]
  (->> buf
       (partition 2)
       (map byte-pair->short)
       (short-array)))

(defn -main []
  (println "Starting up wake word detector...")
  (let [porcupine (init-porcupine)]
    (with-microphone
      (fn [line]
        (let [size 1024
              buf (byte-array size)]
          (println "Listening...")
          (loop []
            (when (> (.read line buf 0 size) 0)
              (when (.processFrame porcupine (bytes->shorts buf))
                (println "Wake word detected!"))
              (recur))))))))
```

We can now create a `deps.edn` for the [Clojure CLI](https://clojure.org/guides/deps_and_cli) (if you haven’t already):

```edn
{:paths ["src/clojure" "src/java"]
 :deps {org.clojure/clojure {:mvn/version "1.9.0"}}}
```

And finally add a line to start our application as the first entry in our makefile:

```makefile
default: wake-word-engine
	LD_LIBRARY_PATH="wake-word-engine/jni" clojure -m wakeup.main
```

As you can see, we need to specify the `LD_LIBRARY_PATH` for our binding which I think varies depending on your operating system.
This works for Linux but I think the name is slightly different for OSX, I’m afraid I have no idea how it would work on Windows.

When I execute `make` I can then say "hey porcupine" to my laptop and get this output:

```console
$ make
LD_LIBRARY_PATH="wake-word-engine/jni" clojure -m wakeup.main
Starting up wake word detector...
Listening...
Wake word detected!
```

I hope this has been helpful and allows you to build voice activated programs of your own.
