package SmartAgricultural.Management.Util;



import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

public class CustomIdGenerator implements IdentifierGenerator {

    private static final AtomicLong counter = new AtomicLong(0);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        String datePrefix = LocalDateTime.now().format(formatter);
        long sequence = counter.incrementAndGet() % 100000; // Reset after 99999

        // Generate format: YYYYMMDD + 6-digit sequence = 14 characters
        return String.format("%s%06d", datePrefix, sequence);
    }
}