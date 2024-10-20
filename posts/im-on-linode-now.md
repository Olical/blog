# I’m on Linode now

And I’m pretty damn happy with it, as happy as you can be with a server I guess.

> Note: There’s a referral link at the bottom of this post.

## Some context

I’ve been running my blog off of [webfaction](https://www.webfaction.com/) for a few years now, I actually used it before then whilst at a previous job too, if memory serves correctly (James? Did we?).

It’s been working well and I’ve actually been hosting a whole suite of sites for a community I was a part of on there (wiki, blog, forum, IRC logs etc). It wasn’t too expensive and it did what I needed. However, I’ve slowly found the need for my _own_ server that _I_ manage and have responsibility for.

Webfaction moved my data to a new server recently (it’s all managed) which prompted me to think about moving, so I started hunting. I was originally going to settle for [DigitalOcean](https://www.digitalocean.com/) after a little search, but a colleague (thanks Alan!) convinced me that [Linode](https://www.linode.com/) offered everything I wanted for the same price as DigitalOcean, but with more memory. I suppose it’s an aside, but as far as I know Linode has been around a lot longer too.

## My Linode

After dumping the source and databases of my blog and __scp__ing it all down to my machine I signed up for Linode. I’m delighted to report that it works well with LastPass and supports two factor authentication, thankfully.

They have recipes called StackScripts to provision a server with common things like Apache, WordPress or nginx, but that list is huge since others can submit them. I guess it’s sort of like community driven Puppet or Chef. I ran the WordPress one and ended up with a server running a LAMP stack with a blank WordPress install in a couple of minutes.

After performing the basic SSH key exchange I followed the [Linode guide to securing your server](https://www.linode.com/docs/security/securing-your-server). This involved creating a non-root user, locking down SSH, implementing a firewall (easy with [ufw](https://www.linode.com/docs/security/firewalls/configure-firewall-with-ufw)) and setting up [fail2ban](https://www.digitalocean.com/community/tutorials/how-fail2ban-works-to-protect-services-on-a-linux-server). I have to say, the Linode documentation is first rate, I’d happily rely on it for non-Linode systems too.

![My Linode’s dashboard. I called it Turing.](/assets/legacy-images/2017/03/Screenshot_2017-03-28_20-09-24-1024x604.png)

Not only does Linode provision and manage the actual image for me, but it also comes with some pretty neat DNS management and Longview, which provides built in, detailed, metrics. They also provide load balancers but I don’t need those (yet). I previously managed my DNS for this domain through Cloudflare, I’ve since moved that to Linode for simplicity and haven’t noticed any issues yet.

![What my Longview dashboard looks like.](/assets/legacy-images/2017/03/Screenshot_2017-03-28_20-12-43.png)

You probably won’t notice or remember (why would you?) but this site used to be _http_. During the migration I cracked open the old [certbot](https://certbot.eff.org/) from the wonderful [Let’s Encrypt](https://letsencrypt.org/) and got that lovely green lock and _https_ you should see at the top of your browser, unless you’re trying to access this site from a potato. In which case the cert probably failed and you may or may not be reading this. ¯\_(ツ)_/¯

So now I have my own Linux server running within Linode (in London, actually!) behind a swanky _https_ URL. So far, I can highly recommend them, if you got this far and you’re as sold as I was, I’d really appreciate a click of my [referral link](https://www.linode.com/?r=6a2af6a5897ea178066c009d778dbb8d847bd813). You don’t need to, but it’d be really cool of you. When in doubt, browse the Linode documentation. Enjoy.
