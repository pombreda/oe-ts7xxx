DESCRIPTION = "Clutter Box2D"
HOMEPAGE = "http://www.clutter-project.org/"
LICENSE = "GPLv2"

DEPENDS = "clutter"

# The main clutter lib is machine specific so we need to be too
PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit autotools pkgconfig gtk-doc

do_stage () {
        autotools_stage_all
}

do_install_append () {
	install -d ${D}${bindir}
	install ${S}/examples/.libs/blockbox ${D}${bindir}
}
