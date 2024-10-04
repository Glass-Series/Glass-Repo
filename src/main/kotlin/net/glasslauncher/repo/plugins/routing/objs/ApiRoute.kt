package net.glasslauncher.repo.plugins.routing.objs

import io.ktor.resources.*

@Resource("/repo/api")
class ApiRoute {

    @Resource("validmodvalues")
    class ValidModValues(val parent: ApiRoute = ApiRoute())

    @Resource("mod/{id}")
    class Mod(val parent: ApiRoute = ApiRoute(), val id: String)
}