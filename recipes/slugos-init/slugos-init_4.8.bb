DESCRIPTION = "SlugOS initial network config via sysconf"
SECTION = "base"
PRIORITY = "required"
LICENSE = "GPL"
DEPENDS = "base-files devio"
RDEPENDS = "busybox devio"
PR = "r5"

SRC_URI = "file://boot/flash \
	   file://boot/disk \
	   file://boot/nfs \
	   file://boot/ram \
	   file://boot/kexec \
	   file://boot/network \
	   file://boot/udhcpc.script \
	   file://initscripts/fixfstab \
	   file://initscripts/syslog.buffer \
	   file://initscripts/syslog.file \
	   file://initscripts/syslog.network \
	   file://initscripts/zleds \
	   file://initscripts/leds_startup \
	   file://initscripts/rmrecovery \
	   file://initscripts/sysconfsetup \
	   file://initscripts/umountinitrd.sh \
	   file://initscripts/loadmodules.sh \
	   file://functions \
	   file://modulefunctions \
	   file://conffiles \
	   file://sysconf \
	   file://leds \
	   file://turnup \
	   "

SBINPROGS = ""
USRSBINPROGS = ""
CPROGS = "${USRSBINPROGS} ${SBINPROGS}"
SCRIPTS = "turnup leds sysconf"
BOOTSCRIPTS = "flash disk nfs ram kexec network udhcpc.script"
INITSCRIPTS = "syslog.buffer syslog.file syslog.network zleds\
	leds_startup rmrecovery sysconfsetup umountinitrd.sh\
	fixfstab loadmodules.sh"

# This just makes things easier...
S="${WORKDIR}"

do_compile() {
	set -ex
	for p in ${CPROGS}
	do
		${CC} ${CFLAGS} -o $p $p.c
	done
	set +ex
}

do_install() {
	set -ex

	# Directories
        install -d ${D}${sysconfdir} \
                   ${D}${sysconfdir}/default \
                   ${D}${sysconfdir}/init.d \
		   ${D}${sysconfdir}/modutils \
                   ${D}${sysconfdir}/udev \
		   ${D}${sbindir} \
		   ${D}${base_sbindir} \
		   ${D}/initrd \
		   ${D}/boot

	# linuxrc
	rm -f ${D}/linuxrc
	ln -s boot/flash ${D}/linuxrc

	# C programs
	for p in ${USRSBINPROGS}
	do
		install -m 0755 $p ${D}${sbindir}/$p
	done
	for p in ${SBINPROGS}
	do
		install -m 0755 $p ${D}${base_sbindir}/$p
	done

	# Shell scripts
	for p in ${SCRIPTS}
	do
		install -m 0755 $p ${D}${base_sbindir}/$p
	done

	#
	# Init scripts
	install -m 0644 functions ${D}${sysconfdir}/default
	install -m 0644 modulefunctions ${D}${sysconfdir}/default
	for s in ${INITSCRIPTS}
	do
		install -m 0755 initscripts/$s ${D}${sysconfdir}/init.d/
	done

	#
	# Boot scripts
	for p in ${BOOTSCRIPTS}
	do
		install -m 0755 boot/$p ${D}/boot
	done

	# Configuration files
	install -m 0644 conffiles ${D}${sysconfdir}/default

	set +ex
}

# If the package is installed on an NSLU2 $D will be empty, in that
# case it is normal to run 'start' and 'stop', but because the conf
# files installed don't actually start or stop anything this is
# unnecessary, so the package postfoo handling is simplified here.
#NB: do not use '08' (etc) for the first argument after start/stop,
# the value is interpreted as an octal number if there is a leading
# zero.
pkg_postinst_slugos-init() {
	opt=
	test -n "$D" && opt="-r $D"
	update-rc.d $opt hwclock.sh		start  8 S . start 45 0 6 .
	update-rc.d $opt umountinitrd.sh	start  9 S .
	update-rc.d $opt fixfstab		start 10 S .
	update-rc.d $opt syslog.buffer		start 11 S . start 49 0 6 .
	update-rc.d $opt sysconfsetup		start 12 S .
	update-rc.d $opt loadmodules.sh		start 21 S .
	update-rc.d $opt syslog.file		start 39 S . start 47 0 6 .
	update-rc.d $opt syslog.network		start 44 S . start 39 0 6 .
	update-rc.d $opt zleds			start 99 S 1 2 3 4 5 . start 89 0 6 . stop  5 0 1 2 3 4 5 6 .
	update-rc.d $opt rmrecovery             start 99 1 2 3 4 5 .
	# bug fix for startup
	update-rc.d $opt leds_startup		start  1 1 2 3 4 5 .
}

pkg_postrm_slugos-init() {
	opt=
	test -n "$D" && opt="-r $D"
	for s in ${INITSCRIPTS}
	do
		update-rc.d $opt "$s" remove
	done
}

FILES_${PN} = "/"

# It is bad to overwrite /linuxrc as it puts the system back to
# a flash boot (and the flash has potentially not been upgraded!)
CONFFILES_${PN} = "/linuxrc ${sysconfdir}/default/conffiles"
