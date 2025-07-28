package disdik.getak.id.Library;

import android.text.InputFilter;
import android.text.Spanned;
import java.util.Arrays;
import java.util.List;

public class TelephoneInputFilter implements InputFilter {

    private final List<String> countryCodes = Arrays.asList(
            "+62", "+1", "+44", "+81", "+91", "+33", "+49"
            // tambahkan kode negara lain sesuai kebutuhan
    );

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        StringBuilder result = new StringBuilder(dest);
        result.replace(dstart, dend, source.subSequence(start, end).toString());
        String currentInput = result.toString();

        if (!currentInput.isEmpty() && !currentInput.startsWith("+")) {
            return "";
        }

        if (currentInput.startsWith("+") && currentInput.length() > 1) {
            boolean isValidCode = false;
            for (String code : countryCodes) {
                if (currentInput.startsWith(code)) {
                    isValidCode = true;
                    break;
                }
            }

            if (!isValidCode && currentInput.length() > 3) {
                return "";
            }
        }

        if (currentInput.length() > 1) {
            String numberPart = currentInput.substring(1);

            if (!numberPart.matches("[0-9]*")) {
                return "";
            }
        }

        return null;
    }
}
