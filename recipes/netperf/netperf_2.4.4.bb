DESCRIPTION="Network performance benchmark including tests for TCP, UDP, sockets, ATM and more."
SECTION = "console/network"
HOMEPAGE = "http://www.netperf.org/"
LICENSE = "netperf"
PR = "r1"

SRC_URI="ftp://ftp.netperf.org/netperf/netperf-${PV}.tar.bz2 \
         file://cpu_set.patch;patch=1 \
         file://init"

inherit update-rc.d autotools

S = "${WORKDIR}/netperf-${PV}"

# cpu_set.patch plus _GNU_SOURCE makes src/netlib.c compile with CPU_ macros
CFLAGS_append = " -DDO_UNIX -DDO_IPV6 -D_GNU_SOURCE"

do_install() {
        install -d ${D}${sbindir} ${D}${bindir} ${D}${sysconfdir}/init.d
        install -m 4755 src/netperf ${D}${bindir}
        install -m 4755 src/netserver ${D}${sbindir}
        install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/netperf

        # man
        install -d ${D}${mandir}/man1/
        install -m 0644 doc/netserver.man ${D}${mandir}/man1/netserver.1
        install -m 0644 doc/netperf.man ${D}${mandir}/man1/netperf.1

        # move scripts to examples directory
        install -d ${D}${docdir}/netperf/examples
        install -m 0644 doc/examples/*_script ${D}${docdir}/netperf/examples/

        # docs ..
        install -m 0644 COPYING ${D}${docdir}/netperf
        install -m 0644 Release_Notes ${D}${docdir}/netperf
        install -m 0644 README ${D}${docdir}/netperf
        install -m 0644 doc/netperf_old.ps ${D}${docdir}/netperf
}

INITSCRIPT_NAME="netperf"
INITSCRIPT_PARAMS="defaults"
