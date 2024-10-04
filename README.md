# This Code Currently Isn't Live
Cause this is still missing a few major features that need to be implemented, or hooked up to the front end.

# Glass Repo
###### Holy shit, about time you published the source like you said you would, calm.

### Purpose

Initially? To host mods for beta versions. 

It has since expanded to cover r1.13 down to rubydung, and supports basically every niche loader I'm aware of.

### Why? Isn't Modrinth and Curseforge Already A Thing?
###### Enjoy the history lesson.

So, some pretext, this project existed before Modrinth was a thing, and if it weren't for one **glaring** issue, it'd be all sunshine and rainbows, and I'd sunset the repo after a while, and instead dedicate compute+my time to archiving stuff and making more mods.

Here I am, though, having rewritten my 2020 legacy code into something that's significantly less god awful and hardcoded, and isn't held together with prayers and duct tape.

The main reason this project was started at all, is that curseforge didn't support pre-release mods, so I initially started with Glass Repo 1.0, which was very, very quickly rewritten into python (2.0), because PHP is a *very* unmaintainable language for someone who hasn't no-life'd it.

I thought python would be better, but with god awful typehint issues, and the weird hacky workarounds I kept finding myself doing to crowbar it into doing OOP things (python: "what's a type?"), I basically put the website onto life support after trying to support it for half a year or so.

Two years later, I was notified by a few helpful folk that curseforge apparently briefly supported mods for pre-release versions, before axing it, and [modrinth still hasn't acknowledged](https://github.com/orgs/modrinth/discussions/106#discussioncomment-8190566) any fabric downstreams that have actual traction.

So last month, I got some inspiration, and went "fuck it, time to do the thing I said I'd do in 2021, and also rewrite this stupid website to actually work well."

### Disclaimers

I won't pretend I know good web dev practises, or that I know Kotlin (nevermind Ktor), so there's a chance that I've committed some real awful code. I'm not sorry.

Glass-repo also isn't a perfect platform. It's run by one guy in their spare time, so please be patient if you find issues, or find yourself desiring features.

The best way to make headway on the above is to make an issue and be polite about it.

### On Monetization

I spend my own money on this service, and therefor, I'd really appreciate it if you don't abuse the site's lenient API limits.

I won't make any hard promises, because that's not how the world works, but here's the lay of the land:
- I won't run ads if I can keep paying without putting myself into financial strain.
- Modders won't be paid. Why should be obvious.

# License

See [LICENSE](LICENSE)

# Copying
(Forking for your own purposes)  
See [COPYING](COPYING)