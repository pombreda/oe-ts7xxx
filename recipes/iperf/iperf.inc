SECTION = "console/network"
DESCRIPTION = "Iperf is a tool to measure maximum TCP bandwidth, allowing the tuning of various parameters and UDP characteristics"
HOMEPAGE = "http://dast.nlanr.net/Projects/Iperf/"
LICENSE = "BSD"
PR = "r0"

inherit autotools

S="${WORKDIR}/iperf-${PV}"

# --disable-threads is needed on epia/x86 with uclibc
do_configure() {
	export ac_cv_func_malloc_0_nonnull=yes
	gnu-configize
	oe_runconf --exec-prefix=${STAGING_DIR_HOST}${layout_exec_prefix}  --disable-threads
}

do_compile() {
	cd ${WORKDIR}/iperf-${PV}
	oe_runmake
}

do_install() {
	cd ${WORKDIR}/iperf-${PV}/src
	oe_runmake DESTDIR=${D} install
}
