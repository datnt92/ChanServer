package com.duduto.util;

/**
 * Class to convert String from Unicode to Ansi
 *
 * @author PhongTn
 *
 */
public class UnicodeToAnsi {

    final private static String[] unicode = {"à", "á", "ả", "ã",
        "ạ", "ă", "ằ", "ắ", "ẳ", "ẵ", "ặ",
        "â", "ầ", "ấ", "ẩ", "ẫ", "ậ", "đ",
        "è", "é", "ẻ", "ẽ", "ẹ", "ê", "ề",
        "ế", "ể", "ễ", "ệ", "ò", "ó", "ỏ",
        "õ", "ọ", "ô", "ồ", "ố", "ổ", "ỗ",
        "ộ", "ơ", "ờ", "ớ", "ở", "ỡ", "ợ",
        "ù", "ú", "ủ", "ũ", "ụ", "ư", "ừ",
        "ứ", "ử", "ữ", "ự", "À", "Á", "Ả",
        "Ã", "Ạ", "Ă", "Ằ", "Ắ", "Ẳ", "Ẵ",
        "Ặ", "Â", "Ầ", "Ấ", "Ẩ", "Ẫ", "Ậ",
        "Đ", "È", "É", "Ẻ", "Ẽ", "Ẹ", "Ê",
        "Ề", "Ế", "Ể", "Ễ", "Ệ", "Ò", "Ó",
        "Ỏ", "Õ", "Ệ", "Ô", "Ồ", "Ố", "Ổ",
        "Ỗ", "Ộ", "Ơ", "Ờ", "Ớ", "Ở", "Ỡ",
        "Ợ", "Ù", "Ú", "Ủ", "Ũ", "Ụ", "Ư",
        "Ừ", "Ứ", "Ử", "Ữ", "Ự", "ì", "í",
        "ỉ", "ĩ", "ị", "Ì", "Í", "Ỉ", "Ĩ",
        "Ị", "ỳ", "ý", "ỷ", "ỹ", "ỵ", "Ỳ",
        "Ý", "Ỷ", "Ỹ", "Ỵ"};
    final private static String[] ansi = {"u00e0", "u00e1", "u1ea3", "u00e3",
        "u1ea1", "u0103", "u1eb1", "u1eaf", "u1eb3", "u1eb5", "u1eb7",
        "u00e2", "u1ea7", "u1ea5", "u1ea9", "u1eab", "u1ead", "u0111",
        "u00e8", "u00e9", "u1ebb", "u1ebd", "u1eb9", "u00ea", "u1ec1",
        "u1ebf", "u1ec3", "u1ec5", "u1ec7", "u00f2", "u00f3", "u1ecf",
        "u00f5", "u1ecd", "u00f4", "u1ed3", "u1ed1", "u1ed5", "u1ed7",
        "u1ed9", "u01a1", "u1edd", "u1edb", "u1edf", "u1ee1", "u1ee3",
        "u00f9", "u00fa", "u1ee7", "u0169", "u1ee5", "u01b0", "u1eeb",
        "u1ee9", "u1eed", "u1eef", "u1ef1", "u00c0", "u00c1", "u1ea2",
        "u00c3", "u1ea0", "u0102", "u1eb0", "u1eae", "u1eb2", "u1eb4",
        "u1eb6", "u00c2", "u1ea6", "u1ea4", "u1ea8", "u1eaa", "u1eac",
        "u0110", "u00c8", "u00c9", "u1eba", "u1ebc", "u1eb8", "u00ca",
        "u1ec0", "u1ebe", "u1ec2", "u1ec4", "u1ec6", "u00d2", "u00d3",
        "u1ece", "u00d5", "u1ec6", "u00d4", "u1ed2", "u1ed0", "u1ed4",
        "u1ed6", "u1ed8", "u01a0", "u1edc", "u1eda", "u1ede", "u1ee0",
        "u1ee2", "u00d9", "u00da", "u1ee6", "u0168", "u1ee4", "u01af",
        "u1eea", "u1ee8", "u1eec", "u1eee", "u1ef0", "u00ec", "u00ed",
        "u1ec9", "u0129", "u1ecb", "u00cc", "u00cd", "u1ec8", "u0128",
        "u1eca", "u1ef3", "u00fd", "u1ef7", "u1ef9", "u1ef5", "u1ef2",
        "u00dd", "u1ef6", "u1ef8", "u1ef4"};

    public static String getAnsiString(String string) {
        String result = string;

        for (int i = 0; i < unicode.length; i++) {
            result = result.replace(unicode[i], "\\" + ansi[i]);
        }
        return result;
    }

    public static String getUnicodeString(String string) {
        String result = string;

        for (int i = 0; i < unicode.length; i++) {
            result = result.replace("\\" + ansi[i], unicode[i]);
        }
        return result;
    }

//    public static void main(String[] args) {
//        String s = UnicodeToAnsi.getUnicodeString("L\u1ed7i nh\u00e0 cung c\u1ea5p, gd n\u00e0y c\u1ea7n \u0111\u1ed1i so\u00e1t");
//        System.out.println(s);
//    }
}
