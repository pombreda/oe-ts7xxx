DESCRIPTION = "TrollTech Makefile Generator"
PRIORITY = "optional"
HOMEPAGE = "http://www.trolltech.com"
SECTION = "devel"
LICENSE = "GPL"
PR = "r7"

# We call 'moc' during the build 
DEPENDS = "uicmoc4-native"

QTVER = "qt-embedded-linux-opensource-src-${QTVERSION}"

SRC_URI = "ftp://ftp.trolltech.com/qt/source/${QTVER}.tar.bz2 \
           file://0001-fix-mkspecs.patch;patch=1 \
           file://qt-config.patch;patch=1 \
           file://use-lflags-last.patch;patch=1 \
           file://linux-oe-qmake.conf"
S = "${WORKDIR}/${QTVER}"

inherit autotools  

export QTDIR = "${S}"
EXTRA_OEMAKE = "-e"

do_configure() {
    # Make sure we regenerate all Makefiles 
    find ${S} -name "Makefile" | xargs rm
    # Install the OE build templates
    for template in linux-oe-g++ linux-uclibc-oe-g++ linux-gnueabi-oe-g++ linux-uclibcgnueabi-oe-g++
    do
        install -d ${S}/mkspecs/$template
        install -m 0644 ${WORKDIR}/linux-oe-qmake.conf ${S}/mkspecs/$template/qmake.conf
        ln -sf ../linux-g++/qplatformdefs.h ${S}/mkspecs/$template/qplatformdefs.h
    done

    QMAKESPEC=
    PLATFORM=${HOST_OS}-oe-g++
    export PLATFORM
    export OE_QMAKE_CC="${CC}"
    export OE_QMAKE_CFLAGS="${CFLAGS}"
    export OE_QMAKE_CXX="${CXX}"
    export OE_QMAKE_CXXFLAGS="-fno-exceptions -fno-rtti ${CXXFLAGS}"
    export OE_QMAKE_LDFLAGS="${LDFLAGS}"
    export OE_QMAKE_LINK="${CCLD}"
    export OE_QMAKE_AR="${AR}"
    export OE_QMAKE_STRIP="${STRIP}"
    export OE_QMAKE_UIC="${STAGING_BINDIR_NATIVE}/uic4"
    export OE_QMAKE_MOC="${STAGING_BINDIR_NATIVE}/moc4"
    export QMAKE_MOC="${STAGING_BINDIR_NATIVE}/moc4"
    export OE_QMAKE_RCC="${STAGING_BINDIR_NATIVE}/rcc4"
    export QMAKE_RCC="${STAGING_BINDIR_NATIVE}/rcc4"
    export OE_QMAKE_QMAKE="${STAGING_BINDIR_NATIVE}/qmake"
    export OE_QMAKE_RPATH="-Wl,-rpath-link,"
    echo yes | ./configure -prefix ${STAGING_DIR_NATIVE}/qt4 ${EXTRA_OECONF} || die "Configuring qt failed"
}

do_compile() {
    :
}

do_stage() {
	install -d ${STAGING_BINDIR_NATIVE}	
	install -m 0755 bin/qmake ${STAGING_BINDIR_NATIVE}/qmake2
	install -m 0755 bin/qmake ${STAGING_BINDIR_NATIVE}/qmake-qt4
	install -d ${STAGING_DATADIR_NATIVE}/qt4
	cp -PfR mkspecs ${STAGING_DATADIR_NATIVE}/qt4/
}
