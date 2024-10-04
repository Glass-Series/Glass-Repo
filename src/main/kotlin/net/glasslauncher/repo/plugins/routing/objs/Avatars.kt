package net.glasslauncher.repo.plugins.routing.objs

import io.ktor.resources.*

@Resource("/avatar/{id}")
class Avatars(val id: String)