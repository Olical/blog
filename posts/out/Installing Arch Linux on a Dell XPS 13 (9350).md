---
alias: installing-arch-linux-on-a-dell-xps-13-9350
tags:
- blog-post
- imported-blog-post
---


I’ve wanted a powerful yet light Linux laptop for a while now, something with a decent CPU and buckets of battery life. I finally found that in the [Dell XPS 13](http://www.dell.com/uk/p/xps-13-9350-laptop/pd). I originally tried to purchase one from Amazon, which failed for some reason (they never explained why they refunded and cancelled, no stock?) but then purchased it from Dell directly. Luckily the new model, 9350, was just released, it’s not the developer edition (one with Ubuntu and no crapware) but it’s good enough.

I went for the 1080p (non-touch, because **EW NO**) screen, 256GB SSD, 8GB RAM and i5 CPU. I felt like it was a good balance of power and price (&lt;£1,000). A lower resolution and CPU clock speed _should_ aid battery life too.

## Out of the box

The first thing you’ll notice is that the actual box it’s shipped in is actually really nice. It’s definitely well protected despite the actual machine being pretty solid. The case is beautiful, thin and metal. It’s incredibly light too, even the charger is solid yet small. I really feel like I’ve got my moneys worth even from just feeling the hardware. I used to think Apple were the only ones that could give me that impression (despite hating their OS and locked down hardware choices), but no, Dell have really hit the spot.

![[/Attachments/imported-blog-posts/legacy-images/2015/11/IMG_20151103_192437-1024x768]]The metal body is complemented by a soft feeling carbon fibre-esc interior. The keyboard feels solid and has a decent layout, control being in the bottom left for example. It’s no ErgoDox, but it’ll do. I can’t emphasise enough how light yet strong this thing feels. When running it’s also silent, I can hardly tell that it’s on!

## link:/Attachments/imported-blog-posts/legacy-images/2015/11/IMG_20151103_192458.jpg[image:/Attachments/imported-blog-posts/legacy-images/2015/11/IMG_20151103_192458-1024x768.jpg[When open,width=640,height=480]]BIOS updates

With the previous model (9343) you had to update the BIOS to at least a version called AO5 to get most components working, [as documented on the Arch wiki](https://wiki.archlinux.org/index.php/Dell_XPS_13_%282015%29#BIOS_updates). This latest version doesn’t appear to have any updates yet but I booted into Windows (shudder, took forever to get through the crappy Windows set up process) to run the Dell tool that checks for me. It found an update but I’m not sure if it actually changed anything. I performed the update anyway.

You can either update through a Windows executable or you can download some file (also a _.exe_ I think?) and boot it from a USB. Dell show you how to do this though. It’s worth checking, BIOS updates appear to make a huge difference with the XPS line and Linux.

The first 9350 BIOS version must be equivalent to the AO5 update from the 9343. I presume. Here’s the update taking place anyway.

## link:/Attachments/imported-blog-posts/legacy-images/2015/11/IMG_20151103_193905.jpg[image:/Attachments/imported-blog-posts/legacy-images/2015/11/IMG_20151103_193905-1024x768.jpg[Updating the BIOS,width=640,height=480]]Fixing the WiFi

As the [Arch wiki mentions](https://wiki.archlinux.org/index.php/Dell_XPS_13_%282015%29#WiFi), the default machine comes with some shoddy Broadcom chip (just like a MacBook!), but you can buy a pretty good Intel one and just swap it out (unlike a MacBook!). I bought my [Intel 7265](http://www.amazon.co.uk/gp/product/B00RK0Q86S?psc=1&redirect=true&ref_=oh_aui_detailpage_o00_s00) from Amazon, I luckily already had [a toolkit](http://www.amazon.co.uk/Cacciavite-Giravite-Acciaio-Allungata-Utensile/dp/B00DIS0LRI/ref=pd_bxgy_23_2?ie=UTF8&refRID=0K3KP6Q1KV75E0HMQZB7) lying around to open the XPS up. You can find the full details to [replace the WiFi on ifixit](https://www.ifixit.com/Teardown/Dell+XPS+13+Teardown/36157), it was surprisingly easy.

Remove the screws around the outer edge as well as the one under the little XPS flap. Pop the base off (took some force, used my Oyster to get some leverage). Unscrew the grounding thing on the chip, remove the two cables which just snap onto little plugs. Slide the chip out and slide the new one in, put the cables back on, screw the thing back down, put the case back on and screw together. Easy. Then it’ll work on boot, the WiFi chip in question is supported by the mainline Linux kernel!

![[/Attachments/imported-blog-posts/legacy-images/2015/11/IMG_20151103_194248-1024x768.jpg]]

![[/Attachments/imported-blog-posts/legacy-images/2015/11/IMG_20151103_195356-1024x768.jpg]]

![[/Attachments/imported-blog-posts/legacy-images/2015/11/IMG_20151103_195548-1024x768.jpg]]

![[/Attachments/imported-blog-posts/legacy-images/2015/11/IMG_20151103_200226-1024x768.jpg]]

## The weird parts

I had to change a few bits in the BIOS (press **F2** at boot to access BIOS menu, **F12** to access boot menu) to get the installer working, as you’d sort of expect. My trial and error process went something like this.

1. Attempt to boot [Antergos](http://antergos.com/) installer USB (it’s essentially [Arch](https://archlinux.org/) with an easy to use installer)
2. Will not boot at all, so make USB with [rufus](https://rufus.akeo.ie/) which prompted to patch some weird files and disable secure boot in BIOS, then it booted
3. Use installer, get to partitioning, no drives, cry
4. Work out that it’s something to do with RAID, go into BIOS -> System Configuration -> SATA operation and set it to disabled
5. Run Antergos installer all the way through (it can see the SSD now!) and fail when it goes to run _mkfs.ext4_, cry
6. Gave up on Antergos, going back to manual Arch, so used rufus on Arch installer and followed normal Arch install guide for UEFI hardware, you’ll also notice that it uses the new _/dev/nvme0n1_ interfaces instead of _/dev/sda_, apparently it’s faster?
7. Arch install went without a hitch

> Edit: I installed it with a full UEFI setup (as suggested by the [Arch beginner install guide](https://wiki.archlinux.org/index.php/beginners'_guide)) using parted and systemd-boot. You can find my bootctl config and fstab in [this gist](https://gist.github.com/Olical/7bf498b46ce1840a0e0a).

I then spent hours trying to work out why X wouldn’t start and in turn LightDM. It was complaining about not being able to detect any screens, well it turns out I needed to [add some kernal parameters](https://wiki.archlinux.org/index.php/Intel_graphics#Driver_not_working_for_Intel_Skylake_chips). I also performed this [early KMS](https://wiki.archlinux.org/index.php/Intel_graphics#Enable_early_KMS) thing, but I’m pretty sure it was the kernal parameter that did it.

![[/Attachments/imported-blog-posts/legacy-images/2015/11/IMG_20151104_200015-1024x768.jpg]]

Everything else is as you would expect, you need the Intel graphics driver and synaptics for touchpad support. All of this is part of a normal Arch install though and all documented on the wiki. The important parts are the BIOS and kernal tweaks however. Once you get those out of the way it should work.

![[/Attachments/imported-blog-posts/legacy-images/2015/11/IMG_20151104_202556-768x1024.jpg]]

## How is it?

Excellent. I haven’t had a single issue after completing the install. [My dotfiles](https://github.com/Olical/dotfiles) all went in without a hitch. The battery life has been extremely impressive already, you should get about five hours of solid use with Firefox chugging away and a large Vim install in the foreground. I’m in Valencia right now and I’ve been using it in cars / airports / flats. This is the first time I’ve charged it since leaving just because I could, I still had over 60% remaining. I made sure to follow [the wiki](https://wiki.archlinux.org/index.php/Dell_XPS_13_%282015%29) and enable everything I could to do with power saving.

I highly recommend this hardware, Linux can clearly work well on it, it just requires a little bit of fiddling. This post is to help others along the way but also to show that it IS possible, Linux does run fine.

![[/Attachments/imported-blog-posts/legacy-images/2015/11/IMG_20151105_010626-1024x768.jpg]]

![[/Attachments/imported-blog-posts/legacy-images/2015/11/IMG_20151105_020549-1024x768.jpg]]

![[/Attachments/imported-blog-posts/legacy-images/2015/11/IMG_20151105_110511-1024x768.jpg]]

I hope someone out there finds this useful! It should save you a bunch of time if you attempt to do the same. I have no idea how easy it would be to install Ubuntu / Debian or any other distro, but if Arch works they probably will too.

Good luck!
