package info.ferrarimarco.uniroma2.is.util;

import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

@Component
public class LocaleDependentDateTimeFormatter implements Formatter<DateTime> {

    @Autowired
    private MessageSource messageSource;

    public DateTime parse(final String text, final Locale locale) {
        return DateTime.parse(text, createDateFormat(locale));
    }

    public String print(final DateTime object, final Locale locale) {
        return object.toString(createDateFormat(locale));
    }

    private DateTimeFormatter createDateFormat(final Locale locale) {
        final String format = this.messageSource.getMessage("date.format", null, locale);
        return new DateTimeFormatterBuilder().appendPattern(format).toFormatter();
    }

}