DESCRIPTION = "DBus-enabled dhcp client"
SECTION = "net"
LICENSE = "GPL"
DEPENDS = "dbus"
RDEPENDS = "dhcp-client"
PR = "r1"

SRC_URI = "https://fedorahosted.org/releases/d/h/dhcdbd/dhcdbd-${PV}.tar.bz2 \
           file://dbus_connection_unref.patch;patch=1 \
           file://paths.patch;patch=1 \
           file://dhcdbd"

inherit update-rc.d

do_compile() {
	CC=${TARGET_SYS}-gcc DESTDIR=${prefix} make
}

do_install() {
	DESTDIR=${D} make install
	install -d ${D}/etc/init.d
	install -m 0755 ${WORKDIR}/dhcdbd ${D}/etc/init.d/
}

INITSCRIPT_NAME = dhcdbd
INITSCRIPT_PARAMS = "start 30 2 3 4 5 . stop 30 0 1 6 ."
FILES_${PN} += "${sysconfdir} ${datadir}/dbus-1 ${base_sbindir}/*"
