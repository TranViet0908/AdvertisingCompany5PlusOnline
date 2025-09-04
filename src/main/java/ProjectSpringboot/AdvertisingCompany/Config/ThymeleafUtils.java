package ProjectSpringboot.AdvertisingCompany.Config;

import org.springframework.stereotype.Component;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Component("utils")
public class ThymeleafUtils {

    public String formatCurrency(Double number) {
        if (number == null) return "0 ₫";

        // Tạo DecimalFormatSymbols cho Việt Nam
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.'); // Dấu chấm phân cách hàng nghìn
        symbols.setDecimalSeparator(',');  // Dấu phẩy phân cách thập phân

        // Tạo formatter với symbols tùy chỉnh
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols);

        return formatter.format(number) + " ₫";
    }

    public String formatCurrencyWithDecimal(Double number) {
        if (number == null) return "0,00 ₫";

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');

        DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);

        return formatter.format(number) + " ₫";
    }

    // Format số thường (không có ký hiệu tiền tệ)
    public String formatNumber(Double number) {
        if (number == null) return "0";

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');

        DecimalFormat formatter = new DecimalFormat("#,##0", symbols);

        return formatter.format(number);
    }

    // Format phần trăm
    public String formatPercent(Double number) {
        if (number == null) return "0%";

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setDecimalSeparator(',');

        DecimalFormat formatter = new DecimalFormat("#0.##", symbols);

        return formatter.format(number) + "%";
    }
}