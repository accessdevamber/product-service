package ecommerce.product_service.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class TimeZoneStuff {

    public static void main(String[] args) {

        //1. First understand the four main Java date/time classes

        //Java type	            Example	                                Meaning
        //LocalDate	            2026-10-22	                            Date only
        //LocalTime	            18:30:00	                            Time only
        //LocalDateTime	        2026-10-22T18:30:00	                    Date + time, without timezone
        //Instant	            2026-10-22T13:00:00Z	                An exact moment on the UTC timeline
        //ZonedDateTime	        2026-10-22T18:30+05:30[Asia/Kolkata]	Date + time + timezone rules
        //OffsetDateTime	    2026-10-22T18:30+05:30	                Date + time + UTC offset

        //The most important difference:
        //- LocalDateTime tells you what a clock reads, but not where.
        //- Instant tells you exactly when something happened, independent of location.

        LocalTime localTime = LocalTime.now();
        LocalTime localTimeWithIndiaZone = LocalTime.now(ZoneId.of("Asia/Kolkata"));
        System.out.println("localTime = " + localTime);//this and below give same for Asia/Kolkata
        System.out.println("localTimeWithIndiaZone = " + localTimeWithIndiaZone);

        LocalTime localTimeWithAmericaZone = LocalTime.now(ZoneId.of("America/New_York"));
        System.out.println("localTimeWithAmericaZone = " + localTimeWithAmericaZone);
        System.out.println("===========================");

        LocalDate localDate = LocalDate.now();
        LocalDate localDateIndiaZone = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        System.out.println("localDate = " + localDate);
        System.out.println("localDateIndiaZone = " + localDateIndiaZone);
        LocalDate localDateAmericaZone = LocalDate.now(ZoneId.of("America/New_York"));
        System.out.println("localDateAmericaZone = " + localDateAmericaZone);
        System.out.println("===========================");


        //The first has no timezone information. The second identifies one precise moment.
        LocalDateTime local = LocalDateTime.parse("2026-10-22T18:30:00");
        System.out.println("local = " + local);
        System.out.println("===========================");

        //identifies one precise moment
        //The Z means UTC, also called zero UTC offset.
        Instant instant = Instant.parse("2026-10-22T13:00:00Z");
        System.out.println("instant = " + instant);
        System.out.println("===========================");


        //Part 1 — Basic Java syntax
        //2. LocalDateTime — creation, String conversion and formatting
        //Import:
        //import java.time.LocalDateTime;
        //import java.time.format.DateTimeFormatter;
        //
        //
        //A. Get the current local date and time

        LocalDateTime now = LocalDateTime.now();
        System.out.println("time now: " + now);
        System.out.println("===========================");

        //Illustrative output on a machine configured for India:
        //2026-10-22T18:30:45.123
        //
        //
        //LocalDateTime.now() uses the JVM's default timezone to obtain the local clock reading,
        // but the resulting object does not remember that timezone.
        //For predictable behavior, you can explicitly specify a timezone:
        LocalDateTime indiaNow = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        System.out.println("indiaNow = " + indiaNow);//2026-10-23T11:33:28.900335
        System.out.println("===========================");


//        B. Convert String to LocalDateTime
//        The default ISO format:
        String input = "2026-10-22T18:30:00";
        LocalDateTime dateTime = LocalDateTime.parse(input);
        System.out.println("dateTime : "+dateTime);//2026-10-22T18:30
        System.out.println("===========================");
        //The trailing seconds may be omitted in the default output when they're zero.


        //For a custom input format:
        String customInput = "22-10-2026 06:30 PM";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu hh:mm a", Locale.ENGLISH);
        //Here:
        //- dd — day of month
        //- MM — month
        //- uuuu — year
        //- hh — 12-hour clock
        //- mm — minutes
        //- a — AM/PM

        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu hh:mm p", Locale.ENGLISH);
        //Exception in thread "main" java.lang.IllegalArgumentException:
        // Pad letter 'p' must be followed by valid pad pattern: dd-MM-uuuu hh:mm p

        LocalDateTime customDateTime = LocalDateTime.parse(customInput, formatter);
        System.out.println("customDateTime = " + customDateTime);
        System.out.println("===========================");


        //C. Convert LocalDateTime to String
        LocalDateTime dateTimeString = LocalDateTime.of(2026, 10, 22, 18, 30);
        String output = dateTimeString.toString();
        System.out.println("dateTimeString output = " + output);
        System.out.println("===========================");

        //Custom formatting:
        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");
        String formatted = dateTimeString.format(customFormatter);
        //using above LocalDateTime dateTimeString = LocalDateTime.of(2026, 10, 22, 18, 30);
        //HH means 24-hour clock.
        System.out.println("custom formatted = " + formatted);
        System.out.println("===========================");


        //D. Add or subtract time
        LocalDateTime localDateTimeNow = LocalDateTime.of(2026, 10, 22, 18, 30);
        LocalDateTime tomorrow = localDateTimeNow.plusDays(1);
        LocalDateTime afterTwoHours = localDateTimeNow.plusHours(2);
        LocalDateTime yesterday = localDateTimeNow.minusDays(1);

        System.out.println("tomorrow = " + tomorrow);//2026-10-23T18:30
        System.out.println("afterTwoHours = " + afterTwoHours);//2026-10-22T20:30
        System.out.println("yesterday = " + yesterday);//2026-10-21T18:30
        System.out.println("===========================");
        //These calculations operate on local calendar and clock values, without considering timezone transitions.


        //3. Instant — creation, String conversion and formatting
        Instant now1 = Instant.now();
        System.out.println("now1 = " + now1);//this will show 5 hours 30 minutes before Indian Std Time
        //Important: Instant.now() doesn't depend on the machine's display timezone.
        // It represents the current point on the UTC timeline.
        System.out.println("===========================");

        //B. Convert String to Instant
        String inputStr = "2026-10-22T13:00:00Z";
        Instant instantObj = Instant.parse(inputStr);
        System.out.println("instantObj = " + instantObj);//2026-10-22T13:00:00Z
        System.out.println("===========================");


        //You can also parse a string containing a UTC offset:
        String inputWithUTCOffset = "2026-10-22T18:30:00+05:30";

        OffsetDateTime offsetDateTime = OffsetDateTime.parse(inputWithUTCOffset);
        System.out.println("offsetDateTime = " + offsetDateTime);
        Instant instant1 = offsetDateTime.toInstant();
        System.out.println("offsetDateTime.toInstant() = " + instant1);//2026-10-22T13:00:00Z doubt
        System.out.println("===========================");


        //C. Convert Instant to String
        Instant instant2 = Instant.parse("2026-10-22T13:00:00Z");
        String result = instant2.toString();
        System.out.println("result = " + result);


        //For custom formatting, first supply a timezone:
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm:ss")
                .withZone(ZoneId.of("Asia/Kolkata"));
        String result1 = formatter1.format(instant2);
        System.out.println("result1 = " + result1);//22-10-2026 18:30:00
        //This is because the instant itself doesn't have local clock fields.
        // The formatter needs a timezone to calculate them.


        //D. Add or subtract time
        Instant now2 = Instant.now();
        System.out.println("now2 = " + now2);
        Instant afterOneHour = now2.plusSeconds(3600);
        Instant afterOneDay = now2.plus(1, ChronoUnit.DAYS);
        Instant beforeOneHour = now2.minusSeconds(3600);
        System.out.println("afterOneHour = " + afterOneHour);
        System.out.println("afterOneDay = " + afterOneDay);
        System.out.println("beforeOneHour = " + beforeOneHour);
        System.out.println("===========================");
        //One ChronoUnit.DAYS applied to an Instant means exactly 24 hours.
        // A calendar day in a timezone with daylight-saving changes isn't always 24 hours.

        
//        Part 2 — Converting between LocalDateTime, Instant and timezones
//        4. LocalDateTime → Instant
//        A LocalDateTime cannot be converted into a unique Instant without supplying a timezone or offset.
//        For example:
        LocalDateTime local1 = LocalDateTime.parse("2026-10-22T18:30:00");

        Instant instant3 = local1
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant();

        System.out.println("instant3 = " + instant3);//2026-10-22T13:00:00Z

        //Why?
        //India is UTC+05:30, so 18:30 in India corresponds to 13:00 UTC.
        //The crucial method is:
        //local.atZone(ZoneId.of("Asia/Kolkata"))
        //
        //
        //It tells Java to interpret that local clock reading according to India's timezone rules.


        //5. Instant → LocalDateTime
        Instant instant4 = Instant.parse("2026-10-22T13:00:00Z");
        LocalDateTime indiaTime = LocalDateTime.ofInstant(
                        instant4,
                        ZoneId.of("Asia/Kolkata")
                );
        System.out.println("indiaTime = " + indiaTime);//2026-10-22T18:30

        //Alternatively:
        LocalDateTime indiaTime1 = instant4
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toLocalDateTime();
        System.out.println("indiaTime1 = " + indiaTime1);
        System.out.println("===========================");
        //Both approaches give the same result.


//        6. Converting between timezones
//        Suppose an order was placed at 6:30 PM in India.
        LocalDateTime indiaLocal = LocalDateTime.parse("2026-10-22T18:30:00");
        ZonedDateTime indiaZoned = indiaLocal.atZone(ZoneId.of("Asia/Kolkata"));
        ZonedDateTime newYorkZoned = indiaZoned.withZoneSameInstant(
                        ZoneId.of("America/New_York")
                );

        System.out.println("indiaLocal = " + indiaLocal);//2026-10-22T18:30
        System.out.println("indiaZoned = " + indiaZoned);//2026-10-22T18:30+05:30[Asia/Kolkata]
        System.out.println("newYorkZoned = " + newYorkZoned);//2026-10-22T09:00-04:00[America/New_York]

        //Notice that the local times differ, but the actual moment is identical.
        //One order event, three clock readings
        //India
        //18:30
        //UTC+05:30
        //
        //
        //UTC
        //13:00
        //UTC+00:00
        //
        //
        //New York
        //09:00
        //UTC−04:00
        //
        //
        //
        //Illustrative values for October 22, 2026. New York is observing daylight-saving time on that date.

//        7. Quick conversion cheat sheet
//        Requirement	Java syntax
//        Current instant	Instant.now()
//        Current local time	LocalDateTime.now()
//        String → Instant	Instant.parse(str)
//        String → LocalDateTime	LocalDateTime.parse(str)
//        Instant → String	instant.toString()
//        LocalDateTime → String	local.toString()
//        LocalDateTime → Instant	local.atZone(zone).toInstant()
//        Instant → LocalDateTime	instant.atZone(zone).toLocalDateTime()
//        Instant → ZonedDateTime	instant.atZone(zone)
//        Change timezone, same instant	zoned.withZoneSameInstant(zone)
//        Epoch milliseconds → Instant	Instant.ofEpochMilli(milliseconds)
//        Instant → epoch milliseconds	instant.toEpochMilli()


        //Part 3 — Real e-commerce scenarios
        //8. Scenario 1: Customer places an order in India
        //Suppose a customer in Bengaluru places an order at:
        //22 October 2026, 6:30 PM IST
        //The backend receives:
        //POST /orders
        //
        //
        //We want to record exactly when the order was created.
        //Our Order entity:
        //@Entity
        //@Table(name = "orders", schema = "public")
        //@Getter
        //@Setter
        //@NoArgsConstructor
        //public class Order {
        //
        //    @Id
        //    private UUID id;
        //
        //    @Column(name = "created_at", nullable = false, updatable = false)
        //    private Instant createdAt;
        //
        //    @PrePersist
        //    public void prePersist() {
        //        createdAt = Instant.now();
        //    }
        //}
        //
        //
        //At the illustrative order time, Java stores:
        //2026-10-22T13:00:00Z
        //
        //
        //PostgreSQL:
        //created_at TIMESTAMPTZ NOT NULL
        //
        //
        //The database stores the instant. When queried, PostgreSQL displays it according to the database session's timezone.
        //For example, with a UTC session:
        //SET TIME ZONE 'UTC';
        //
        //SELECT created_at FROM public.orders;
        //
        //
        //Output:
        //2026-10-22 13:00:00+00
        //
        //
        //With an India session:
        //SET TIME ZONE 'Asia/Kolkata';
        //
        //SELECT created_at FROM public.orders;
        //
        //
        //Output:
        //2026-10-22 18:30:00+05:30
        //
        //
        //The stored instant hasn't changed. Only the displayed timezone has changed.
        //This is a major benefit of PostgreSQL TIMESTAMPTZ.


        //9. Scenario 2: An Indian customer and a US customer place orders simultaneously
        //Imagine two customers:
        //- Customer A is in Bengaluru, India.
        //- Customer B is in New York, USA.
        //Both place an order at exactly the same moment.
        //India order
        //6:30 PM
        //22 Oct 2026 · IST
        //
        //
        //US order
        //9:00 AM
        //22 Oct 2026 · New York
        //
        //
        //
        //
        //Both represent 2026-10-22T13:00:00Z
        //
        //
        //What goes wrong with LocalDateTime?
        //Suppose each server stores its local clock time without timezone information.
        //Order	Stored LocalDateTime
        //India	2026-10-22T18:30:00
        //US	2026-10-22T09:00:00
        //If you sort by these values, the US order appears 9.5 hours earlier, even though both happened simultaneously.
        //Worse, suppose one order is placed in New York at 9:05 AM, five minutes after the India order. Its local timestamp 09:05 still sorts before the India order's 18:30.
        //That makes order chronology, payment reconciliation, event processing and audit logs unreliable.
        //Important nuance: LocalDateTime isn't inherently incorrect for global systems. You can use it safely if every application consistently interprets it as UTC. But the timezone convention exists outside the type, so mistakes are easier.
        //Instant makes the intent much clearer.


        //10. Scenario 3: Display an order in the customer's timezone
        //Our database stores:
        //2026-10-22T13:00:00Z
        //
        //
        //But we want each customer to see their own local order time.
        //Instant orderTime =
        //        Instant.parse("2026-10-22T13:00:00Z");
        //
        //ZoneId india = ZoneId.of("Asia/Kolkata");
        //ZoneId newYork = ZoneId.of("America/New_York");
        //
        //System.out.println(orderTime.atZone(india));
        //System.out.println(orderTime.atZone(newYork));
        //
        //
        //Output:
        //2026-10-22T18:30+05:30[Asia/Kolkata]
        //2026-10-22T09:00-04:00[America/New_York]
        //
        //
        //In our API, we could return:
        //{
        //  "orderId": "example-order-id",
        //  "createdAt": "2026-10-22T13:00:00Z"
        //}
        //
        //
        //The frontend can then convert the timestamp into the user's preferred timezone.
        //Alternatively, the backend can return a formatted local timestamp if the API requires it.
        //For example:
        //DateTimeFormatter formatter =
        //        DateTimeFormatter.ofPattern("dd MMM uuuu, hh:mm a")
        //                .withLocale(Locale.ENGLISH);
        //
        //String displayTime = orderTime
        //        .atZone(ZoneId.of("Asia/Kolkata"))
        //        .format(formatter);
        //
        //System.out.println(displayTime);
        //
        //
        //Output:
        //22 Oct 2026, 06:30 PM
        //
        //
        //We should also display the timezone label, such as IST, when presenting timestamps to customers.


        //Part 4 — More complicated timezone problems
        //11. Daylight-saving time (DST)
        //India doesn't currently observe daylight-saving time, but countries such as the US do.
        //For New York:
        //Season	Typical UTC offset
        //Winter	UTC−05:00
        //Summer	UTC−04:00
        //That's why hardcoding a fixed offset is risky.
        //Avoid:
        //ZoneOffset.ofHours(-5)
        //
        //
        //when the requirement is specifically New York local time year-round.
        //Prefer:
        //ZoneId.of("America/New_York")
        //
        //
        //Java applies the relevant timezone rules for the date.
        //The duplicate-time problem
        //On November 1, 2026, New York clocks move backward from 2:00 AM daylight time to 1:00 AM standard time.
        //That means the local time:
        //2026-11-01 01:30 AM
        //
        //
        //occurs twice.
        //It could mean either:
        //2026-11-01T05:30:00Z  (01:30 EDT)
        //2026-11-01T06:30:00Z  (01:30 EST)
        //
        //
        //A LocalDateTime alone cannot distinguish these two moments.
        //An Instant can.
        //Similarly, when clocks move forward in spring, certain local clock times don't exist.
        //This matters when scheduling deliveries, payments, sales campaigns or appointments.


        //12. Order creation vs scheduled delivery
        //Different business requirements need different time representations.
        //Business field	Recommended Java type	Why
        //order.createdAt	Instant	Exact event timestamp
        //payment.paidAt	Instant	Exact payment timestamp
        //product.updatedAt	Instant	Audit timestamp
        //shipment.dispatchedAt	Instant	Exact dispatch timestamp
        //customer.dateOfBirth	LocalDate	Calendar date
        //store.openingTime	LocalTime	Local clock time
        //deliveryAppointment	ZonedDateTime or LocalDateTime + ZoneId	Local time and timezone rules matter
        //saleStartAt	Instant	Fixed worldwide start moment
        //Suppose a delivery is scheduled for 9 AM New York time next Monday.
        //If you store only:
        //09:00
        //
        //
        //you don't know the date or timezone.
        //If you store only:
        //2026-11-02T09:00
        //
        //
        //you know the date and time, but not where.
        //For a future local appointment, you may need:
        //LocalDateTime appointment =
        //        LocalDateTime.parse("2026-11-02T09:00:00");
        //
        //ZoneId deliveryZone =
        //        ZoneId.of("America/New_York");
        //
        //ZonedDateTime scheduled =
        //        appointment.atZone(deliveryZone);
        //
        //
        //For important scheduling workflows, validate that the local time is valid and unambiguous in the chosen timezone.
        // Java's default atZone() behavior can resolve gaps and overlaps automatically, which may not match the customer's intention.
        //You can persist both the resolved instant and the intended timezone ID when future scheduling semantics matter.


        //Part 5 — PostgreSQL mapping for our Product Service
        //13. Which SQL type should we use?
        //Java	PostgreSQL	Recommended usage
        //LocalDate	DATE	Birth date, calendar date
        //LocalTime	TIME	Opening hours
        //LocalDateTime	TIMESTAMP	Local wall-clock date and time
        //Instant	TIMESTAMPTZ	Created/updated/event timestamps
        //OffsetDateTime	TIMESTAMPTZ	Absolute timestamp with offset-based Java representation
        //For OffsetDateTime mapped to TIMESTAMPTZ, PostgreSQL preserves the instant, not the original offset. Store the original timezone separately if it matters.
        //Our current Product entity:
        //@Column(name = "created_at", nullable = false, updatable = false)
        //private Instant createdAt;
        //
        //@Column(name = "updated_at", nullable = false)
        //private Instant updatedAt;
        //
        //
        //Flyway:
        //created_at TIMESTAMPTZ NOT NULL,
        //updated_at TIMESTAMPTZ NOT NULL
        //
        //
        //This is the correct pairing for our audit timestamps.
        //Practical Spring Boot example
        //@PrePersist
        //public void prePersist() {
        //    Instant now = Instant.now();
        //
        //    if (id == null) {
        //        id = UuidV7Generator.generate();
        //    }
        //
        //    if (createdAt == null) {
        //        createdAt = now;
        //    }
        //
        //    updatedAt = now;
        //}
        //
        //@PreUpdate
        //public void preUpdate() {
        //    updatedAt = Instant.now();
        //}
        //
        //
        //On creation, both timestamps are set.
        //On an entity update, updatedAt is refreshed while createdAt remains unchanged.
        //Remember that JPA callbacks aren't triggered by direct SQL or bulk JPQL updates.

        System.out.println("==============================");
        System.out.println("==============================");
        System.out.println("==============================");

        ZoneId india = ZoneId.of("Asia/Kolkata");
        ZoneId newYork = ZoneId.of("America/New_York");

        // 1. Customer places an order at 6:30 PM in India
        String inputTime = "2026-10-22T18:30:00";

        // 2. Parse the string into LocalDateTime
        LocalDateTime indiaLocalDateTime = LocalDateTime.parse(inputTime);

        // 3. Attach the actual timezone
        ZonedDateTime indiaZonedDateTime = indiaLocalDateTime.atZone(india);

        // 4. Convert to a global instant
        Instant orderInstant = indiaZonedDateTime.toInstant();

        // 5. Display the same order time in New York
        ZonedDateTime usTime = orderInstant.atZone(newYork);

        // 6. Format for the Indian customer
        DateTimeFormatter formatterObj = DateTimeFormatter.ofPattern(
                        "dd MMM uuuu, hh:mm a",
                        Locale.ENGLISH
                );

        String displayTime = indiaZonedDateTime.format(formatterObj);

        System.out.println("inputTime: " + inputTime);
        System.out.println("India LocalDateTime: " + indiaLocalDateTime);
        System.out.println("India ZonedDateTime: " + indiaZonedDateTime);
        System.out.println("Instant for DB: " + orderInstant);
        System.out.println("New York time: " + usTime);
        System.out.println("Customer display: " + displayTime);


        //15. Final rules for our e-commerce project
        //- Use Instant for events that happened at a specific moment:
        // order creation, payment completion, product creation/update and shipment dispatch.
        //- Use TIMESTAMPTZ in PostgreSQL for these timestamps.
        //- Use LocalDateTime when you intentionally mean a local calendar date and clock time, and separately know the applicable timezone.
        //- Use ZoneId, such as Asia/Kolkata or America/New_York, when converting or scheduling based on geographic timezones.
        //- Return ISO 8601 timestamps with Z or an explicit offset in APIs rather than ambiguous local timestamps.
        //- Never assume the server's timezone is the customer's timezone. A customer in India, the US or elsewhere should see their own local representation of the same stored instant.
        //For Product Service, we don't need to change our existing entity or migration. Instant in Java and TIMESTAMPTZ in PostgreSQL is the approach we'll continue using.




    }
}
