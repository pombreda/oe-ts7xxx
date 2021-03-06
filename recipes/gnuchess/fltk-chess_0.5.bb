DESCRIPTION = "fltk-chess is a frontend for the Gnuchess chess playing engine."
AUTHOR = "Andreas Holzer <a.holzer@cheese.at>"
SECTION = "x11/games"
PRIORITY = "optional"
LICENSE = "GPL"
DEPENDS = "fltk"
RDEPENDS = "gnuchess"


SRC_URI = "http://members.cheese.at/woody/fltk-chess/fltk-chess-${PV}.tgz \
           file://fix_cast_error_and_gnuchess_exec.patch;patch=1 \
           file://fltk-chess.desktop"
S = "${WORKDIR}/fltk-chess/"


do_compile() {
        `fltk-config --cxx --cxxflags --ldflags --use-images` -o fltk-chess \
         fltk-chess.cxx
}

do_install() {
        install -d ${D}${bindir}
        install -d ${D}${datadir}/pixmaps/fltk-chess/
        install -d ${D}${datadir}/applications/
        install -m 0755 ${S}/fltk-chess ${D}${bindir}
        install -m 0644 ${S}/bitmaps/*.pnx ${D}${datadir}/pixmaps/fltk-chess/
        install -m 0644 ${S}/bitmaps/{user,computer}.png \
                ${D}${datadir}/pixmaps/fltk-chess/
        install -m 0644 ${S}/bitmaps/fltk-chess.png  ${D}${datadir}/pixmaps/
        install -m 0644 ${WORKDIR}/fltk-chess.desktop  ${D}${datadir}/applications/
}


