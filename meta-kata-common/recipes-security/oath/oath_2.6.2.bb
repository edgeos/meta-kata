# GIT Repo can be found here: https://gitlab.com/oath-toolkit/oath-toolkit/tree/master
SUMMARY = "Oath-Toolkit - One-time password components"
SECTION = "Install Oath-Toolkit"
PROVIDES = "oath"
RPROVIDES_{PN} = "oath"
LICENSE = "GPLv3+"

inherit autotools cmake pkgconfig

SRC_URI = " \
    http://download.savannah.nongnu.org/releases/oath-toolkit/oath-toolkit-2.6.2.tar.gz \
    file://gcc7.patch \
    "
SRC_URI[md5sum] = "4a05cd4768764843bd5493609a6bdb17"
SRC_URI[sha256sum] = "b03446fa4b549af5ebe4d35d7aba51163442d255660558cd861ebce536824aa0"
LIC_FILES_CHKSUM = "file://COPYING;md5=62e1e33aebac5b1bc9fc48a866e2f61b"



DEPENDS += " libxml2 xmlsec1 "

RDEPENDS_{PN} += " libxml2 xmlsec1 "

S = "${WORKDIR}/oath-toolkit-2.6.2"

do_configure() {
    cd ${S}

    rm -rf build
    mkdir build
    cd build

    ../configure --prefix=${D}/usr --host=${TARGET_ARCH}  # --target=${TARGET_ARCH}
}

do_compile() {
    #(cd ${STAGING_DIR_TARGET}/usr/include && ln -s ./libxml2/libxml ./libxml)
    cd ${S}/build
    make -d  XML_CPPFLAGS="" XML_LIBS=""  # XML_LIBS="${D}/usr/lib/libxml2.so" XML_CPPFLAGS=" -I${D}/usr/include/libxml2/ "
    # XML_CPPFLAGS="" XML_LIBS="" 
    # make check
    #for i in $(find ${D} -type f) ; do
    #    sed -i -e s:${STAGING_DIR_TARGET}::g \
    #           -e s:/${TARGET_SYS}::g \
    #           $i
    #done
}

do_install() {
    cd ${S}/build
    make install
    rm -rf ${D}/usr/lib/pkgconfig
    chown root:root -R ${D}
}

FILES_${PN} += " \
/usr  \
/usr/lib  \
/usr/lib/liboath.a  \
/usr/lib/libpskc.a  \
/usr/include  \
/usr/include/liboath  \
/usr/include/liboath/oath.h  \
/usr/include/pskc  \
/usr/include/pskc/errors.h  \
/usr/include/pskc/keypackage.h  \
/usr/include/pskc/pskc.h  \
/usr/include/pskc/global.h  \
/usr/include/pskc/enums.h  \
/usr/include/pskc/version.h  \
/usr/include/pskc/container.h  \
/usr/include/pskc/exports.h  \
/usr/share  \
/usr/share/xml  \
/usr/share/xml/pskc  \
/usr/share/xml/pskc/xenc-schema.xsd  \
/usr/share/xml/pskc/catalog-pskc.xml  \
/usr/share/xml/pskc/xmldsig-core-schema.xsd  \
/usr/share/xml/pskc/pskc-schema.xsd  \
/usr/share/man  \
/usr/share/man/man1  \
/usr/share/man/man1/oathtool.1  \
/usr/share/man/man1/pskctool.1  \
/usr/share/man/man3  \
/usr/share/man/man3/pskc_set_key_data_secret.3  \
/usr/share/man/man3/pskc_get_cryptomodule_id.3  \
/usr/share/man/man3/oath_hotp_validate_callback.3  \
/usr/share/man/man3/pskc_set_device_startdate.3  \
/usr/share/man/man3/pskc_set_key_algparm_chall_min.3  \
/usr/share/man/man3/pskc_set_device_manufacturer.3  \
/usr/share/man/man3/pskc_get_key_algparm_chall_checkdigits.3  \
/usr/share/man/man3/pskc_set_key_id.3  \
/usr/share/man/man3/pskc_get_key_data_timedrift.3  \
/usr/share/man/man3/oath_check_version.3  \
/usr/share/man/man3/pskc_get_key_userid.3  \
/usr/share/man/man3/pskc_set_key_algorithm.3  \
/usr/share/man/man3/pskc_get_device_issueno.3  \
/usr/share/man/man3/oath_totp_validate4_callback.3  \
/usr/share/man/man3/pskc_get_key_profileid.3  \
/usr/share/man/man3/pskc_check_version.3  \
/usr/share/man/man3/pskc_pinusagemode2str.3  \
/usr/share/man/man3/pskc_set_device_issueno.3  \
/usr/share/man/man3/pskc_set_key_policy_pinkeyid.3  \
/usr/share/man/man3/pskc_set_key_data_b64secret.3  \
/usr/share/man/man3/oath_strerror.3  \
/usr/share/man/man3/pskc_get_key_policy_expirydate.3  \
/usr/share/man/man3/pskc_get_key_policy_pinusagemode.3  \
/usr/share/man/man3/oath_totp_validate3_callback.3  \
/usr/share/man/man3/pskc_set_device_model.3  \
/usr/share/man/man3/pskc_get_key_algparm_resp_length.3  \
/usr/share/man/man3/pskc_set_key_algparm_resp_length.3  \
/usr/share/man/man3/oath_bin2hex.3  \
/usr/share/man/man3/pskc_set_key_friendlyname.3  \
/usr/share/man/man3/oath_done.3  \
/usr/share/man/man3/pskc_done.3  \
/usr/share/man/man3/oath_hex2bin.3  \
/usr/share/man/man3/pskc_set_key_policy_numberoftransactions.3  \
/usr/share/man/man3/pskc_get_key_policy_startdate.3  \
/usr/share/man/man3/pskc_add_keypackage.3  \
/usr/share/man/man3/oath_strerror_name.3  \
/usr/share/man/man3/oath_totp_validate3.3  \
/usr/share/man/man3/pskc_set_key_policy_pinmaxfailedattempts.3  \
/usr/share/man/man3/pskc_set_key_policy_pinmaxlength.3  \
/usr/share/man/man3/oath_base32_decode.3  \
/usr/share/man/man3/pskc_valueformat2str.3  \
/usr/share/man/man3/pskc_get_key_policy_pinencoding.3  \
/usr/share/man/man3/pskc_verify_x509crt.3  \
/usr/share/man/man3/pskc_get_key_data_counter.3  \
/usr/share/man/man3/oath_totp_generate.3  \
/usr/share/man/man3/oath_hotp_validate.3  \
/usr/share/man/man3/pskc_get_key_algparm_suite.3  \
/usr/share/man/man3/pskc_get_key_issuer.3  \
/usr/share/man/man3/pskc_get_key_data_secret.3  \
/usr/share/man/man3/pskc_free.3  \
/usr/share/man/man3/pskc_get_signed_p.3  \
/usr/share/man/man3/pskc_set_device_userid.3  \
/usr/share/man/man3/pskc_set_key_algparm_resp_encoding.3  \
/usr/share/man/man3/pskc_get_device_model.3  \
/usr/share/man/man3/pskc_set_key_algparm_resp_checkdigits.3  \
/usr/share/man/man3/pskc_set_key_policy_expirydate.3  \
/usr/share/man/man3/pskc_get_device_expirydate.3  \
/usr/share/man/man3/pskc_set_key_data_timeinterval.3  \
/usr/share/man/man3/pskc_keyusage2str.3  \
/usr/share/man/man3/pskc_set_key_policy_startdate.3  \
/usr/share/man/man3/pskc_get_key_policy_pinmaxlength.3  \
/usr/share/man/man3/oath_totp_validate2.3  \
/usr/share/man/man3/pskc_set_key_policy_pinencoding.3  \
/usr/share/man/man3/pskc_get_key_friendlyname.3  \
/usr/share/man/man3/pskc_output.3  \
/usr/share/man/man3/pskc_get_key_data_b64secret.3  \
/usr/share/man/man3/oath_totp_validate.3  \
/usr/share/man/man3/pskc_get_device_serialno.3  \
/usr/share/man/man3/oath_hotp_generate.3  \
/usr/share/man/man3/pskc_get_device_devicebinding.3  \
/usr/share/man/man3/pskc_set_version.3  \
/usr/share/man/man3/pskc_init.3  \
/usr/share/man/man3/pskc_set_key_userid.3  \
/usr/share/man/man3/oath_authenticate_usersfile.3  \
/usr/share/man/man3/pskc_set_id.3  \
/usr/share/man/man3/pskc_set_key_algparm_chall_max.3  \
/usr/share/man/man3/pskc_set_key_data_timedrift.3  \
/usr/share/man/man3/pskc_strerror.3  \
/usr/share/man/man3/pskc_get_key_data_timeinterval.3  \
/usr/share/man/man3/pskc_get_id.3  \
/usr/share/man/man3/pskc_global_init.3  \
/usr/share/man/man3/pskc_get_key_policy_pinkeyid.3  \
/usr/share/man/man3/pskc_set_key_issuer.3  \
/usr/share/man/man3/pskc_get_device_startdate.3  \
/usr/share/man/man3/pskc_set_key_algparm_chall_checkdigits.3  \
/usr/share/man/man3/pskc_get_keypackage.3  \
/usr/share/man/man3/pskc_build_xml.3  \
/usr/share/man/man3/pskc_set_key_algparm_chall_encoding.3  \
/usr/share/man/man3/pskc_sign_x509.3  \
/usr/share/man/man3/oath_base32_encode.3  \
/usr/share/man/man3/oath_totp_validate4.3  \
/usr/share/man/man3/pskc_parse_from_memory.3  \
/usr/share/man/man3/oath_totp_validate2_callback.3  \
/usr/share/man/man3/oath_totp_generate2.3  \
/usr/share/man/man3/pskc_get_device_manufacturer.3  \
/usr/share/man/man3/pskc_get_version.3  \
/usr/share/man/man3/pskc_set_key_reference.3  \
/usr/share/man/man3/pskc_set_device_expirydate.3  \
/usr/share/man/man3/pskc_get_key_data_time.3  \
/usr/share/man/man3/pskc_set_key_algparm_suite.3  \
/usr/share/man/man3/pskc_get_key_algparm_chall_max.3  \
/usr/share/man/man3/pskc_set_key_data_counter.3  \
/usr/share/man/man3/pskc_global_done.3  \
/usr/share/man/man3/pskc_str2valueformat.3  \
/usr/share/man/man3/pskc_set_device_devicebinding.3  \
/usr/share/man/man3/pskc_set_key_policy_pinminlength.3  \
/usr/share/man/man3/pskc_get_key_algorithm.3  \
/usr/share/man/man3/pskc_get_key_policy_pinminlength.3  \
/usr/share/man/man3/pskc_set_key_data_time.3  \
/usr/share/man/man3/pskc_set_key_profileid.3  \
/usr/share/man/man3/pskc_get_key_algparm_resp_encoding.3  \
/usr/share/man/man3/oath_totp_validate_callback.3  \
/usr/share/man/man3/pskc_strerror_name.3  \
/usr/share/man/man3/pskc_get_key_policy_keyusages.3  \
/usr/share/man/man3/pskc_set_cryptomodule_id.3  \
/usr/share/man/man3/pskc_get_device_userid.3  \
/usr/share/man/man3/pskc_set_device_serialno.3  \
/usr/share/man/man3/pskc_validate.3  \
/usr/share/man/man3/pskc_get_key_algparm_resp_checkdigits.3  \
/usr/share/man/man3/pskc_get_key_reference.3  \
/usr/share/man/man3/pskc_get_key_policy_pinmaxfailedattempts.3  \
/usr/share/man/man3/pskc_str2pinusagemode.3  \
/usr/share/man/man3/pskc_global_log.3  \
/usr/share/man/man3/pskc_get_key_algparm_chall_min.3  \
/usr/share/man/man3/pskc_set_key_policy_keyusages.3  \
/usr/share/man/man3/oath_init.3  \
/usr/share/man/man3/pskc_str2keyusage.3  \
/usr/share/man/man3/pskc_get_key_algparm_chall_encoding.3  \
/usr/share/man/man3/pskc_set_key_policy_pinusagemode.3  \
/usr/share/man/man3/pskc_get_key_policy_numberoftransactions.3  \
/usr/share/man/man3/pskc_get_key_id.3  \
/usr/bin  \
/usr/bin/pskctool  \
/usr/bin/oathtool  \
"
