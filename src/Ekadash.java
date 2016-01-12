import java.util.*;

/**
 * Calculating Fasting Days by moon
 *
 * @version 1.0
 * @author Glushkov Akim aka Acyuta [acyuta.lpt@gmail.com]
 */
public class Ekadash {


    public static final double MY_PI = 3.14159265358979323846;
    public static final double EPOCH = 2444238.5;    /* 1980 January 0.0. */
    public static final double SUN_ELONG_EPOCH = 278.833540;   /* Ecliptic longitude of the Sun at epoch 1980.0. */
    public static final double SUN_ELONG_PERIGEE = 282.596403;   /* Ecliptic longitude of the Sun at perigee. */
    public static final double ECCENT_EARTH_ORBIT = 0.016718;     /* Eccentricity of Earth's orbit. */
    public static final double KEPLER_EPSILON = 1E-6;         /* Accurancy of the Kepler equation. */
    public static final double MOON_MEAN_LONGITUDE_EPOCH = 64.975464;    /* Moon's mean lonigitude at the epoch. */
    public static final double MOON_MEAN_LONGITUDE_PERIGREE = 349.383063;   /* Mean longitude of the perigee at the epoch. */
    public static final double SYNMONTH = 29.53058868;    /* Synodic month (new Moon to new Moon) */
    public static final int EARTH_SECONDS_MOON_DAY = 97567; //Количество секунд в дне

    //private instance fields
    private Calendar _curCal;
    private double _JD;
    private double _phase;
    private static double _moonAgeAsDays;
    private ArrayList<Fasting> array;

    public Ekadash(Calendar instance) {
        _curCal = instance;
        array = new ArrayList<>(50);
    }

    /**
     * Returns current phase as double value
     * Uses class Calendar field _curCal
     */
    public double getPhase() {
        return getPhase(_curCal);
    }

    public double getPhase(Calendar calendar) {
        _curCal = calendar;
        _JD = calendarToJD(calendar);
        _phase = phase(_JD);
        return _phase;
    }

    /*
       Solves the equation of Kepler.
    */
    public static double kepler(double m) {
        double e;
        double delta;
        e = m = TORAD(m);
        do {
            delta = e - ECCENT_EARTH_ORBIT * Math.sin(e) - m;
            e -= delta / (1.0 - ECCENT_EARTH_ORBIT * Math.cos(e));
        } while (Math.abs(delta) - KEPLER_EPSILON > 0.0);

        return (e);
    }

    /**
     * UCTTOJ  --  Convert GMT date and time to astronomical
     * Julian time (i.e. Julian date plus day fraction,
     * expressed as a double).
     *
     * @param cal Calendar object
     * @return JD float Julian date
     * <p>Converted to Java by vriolk@gmail.com from original file mooncalc.c,
     * part of moontool http://www.fourmilab.ch/moontoolw/moont16s.zip</p>
     */
    public static double calendarToJD(Calendar cal) {

        /* Algorithm as given in Meeus, Astronomical Algorithms, Chapter 7, page 61*/
        long year = cal.get(Calendar.YEAR);
        int mon = cal.get(Calendar.MONTH);
        int mday = cal.get(Calendar.DATE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);

        int a, b, m;
        long y;

        m = mon + 1;
        y = year;

        if (m <= 2) {
            y--;
            m += 12;
        }

        /* Determine whether date is in Julian or Gregorian
         * calendar based on canonical date of calendar reform.
         */
        if ((year < 1582) || ((year == 1582) && ((mon < 9) || (mon == 9 && mday < 5)))) {
            b = 0;
        } else {
            a = ((int) (y / 100));
            b = 2 - a + (a / 4);
        }

        return (((long) (365.25 * (y + 4716))) + ((int) (30.6001 * (m + 1))) +
                mday + b - 1524.5) +
                ((sec + 60L * (min + 60L * hour)) / 86400.0);
    }

    public String getMoonAgeAsDays() {

        int aom_d = (int) _moonAgeAsDays;
        int aom_h = (int) (24 * (_moonAgeAsDays - Math.floor(_moonAgeAsDays)));
        int aom_m = (int) (1440 * (_moonAgeAsDays - Math.floor(_moonAgeAsDays))) % 60;

        return "" + aom_d + (aom_d == 1 ? " day, " : " days, ") +
                aom_h + (aom_h == 1 ? " hour, " : " hours, ") +
                aom_m + (aom_m == 1 ? " minute" : " minutes");
    }

    public double getMoonAge(Calendar calendar) {
        this._curCal = calendar;
        getPhase(calendar);
        return _moonAgeAsDays;
    }

    public double getMoonAge() {
        return _moonAgeAsDays;
    }

    /*
     * Some useful mathematical functions used by John Walkers `phase()' function.
     */
    public static double FIXANGLE(double a) {
        return (a) - 360.0 * (Math.floor((a) / 360.0));
    }

    public static double TORAD(double d) {
        return (d) * (MY_PI / 180.0);
    }

    public static double TODEG(double r) {
        return (r) * (180.0 / MY_PI);
    }

    /**
     * <p>Calculates the phase of the Moon and returns the illuminated fraction of
     * the Moon's disc as a value within the range of -99.9~...0.0...+99.9~,
     * which has a negative sign in case the Moon wanes, otherwise the sign
     * is positive.  The New Moon phase is around the 0.0 value and the Full
     * Moon phase is around the +/-99.9~ value.  The argument is the time for
     * which the phase is requested, expressed as a Julian date and fraction.</p>
     * <p>This function is taken from the program "moontool" by John Walker,
     * February 1988, which is in the public domain.  So see it for more
     * information!  It is adapted (crippled) and `pretty-printed' to the
     * requirements of Gcal, which means it is lacking all the other useful
     * computations of astronomical values of the original code.</p>
     * <p>
     * <p>Here is the blurb from "moontool":</p>
     * <p>...The algorithms used in this program to calculate the positions Sun
     * and Moon as seen from the Earth are given in the book "Practical Astronomy
     * With Your Calculator" by Peter Duffett-Smith, Second Edition,
     * Cambridge University Press, 1981. Ignore the word "Calculator" in the
     * title; this is an essential reference if you're interested in
     * developing software which calculates planetary positions, orbits,
     * eclipses, and the like. If you're interested in pursuing such
     * programming, you should also obtain:</p>
     * <p>
     * <p>"Astronomical Formulae for Calculators" by Jean Meeus, Third Edition,
     * Willmann-Bell, 1985. A must-have.</p>
     * <p>
     * </p>"Planetary Programs and Tables from -4000 to +2800" by Pierre
     * Bretagnon and Jean-Louis Simon, Willmann-Bell, 1986. If you want the
     * utmost (outside of JPL) accuracy for the planets, it's here.</p>
     * <p>
     * <p>"Celestial BASIC" by Eric Burgess, Revised Edition, Sybex, 1985. Very
     * cookbook oriented, and many of the algorithms are hard to dig out of
     * the turgid BASIC code, but you'll probably want it anyway.</p>
     * <p>
     * <p>Many of these references can be obtained from Willmann-Bell, P.O. Box
     * 35025, Richmond, VA 23235, USA. Phone: (804) 320-7016. In addition
     * to their own publications, they stock most of the standard references
     * for mathematical and positional astronomy.</p>
     * <p>
     * <p>This program was written by:</p>
     * <p>
     * <p>John Walker<br>
     * Autodesk, Inc.<br>
     * 2320 Marinship Way<br>
     * Sausalito, CA 94965<br>
     * (415) 332-2344 Ext. 829</p>
     * <p>
     * <p>Usenet: {sun!well}!acad!kelvin</p>
     * <p>
     * <p>This program is in the public domain: "Do what thou wilt shall be the
     * whole of the law". I'd appreciate receiving any bug fixes and/or
     * enhancements, which I'll incorporate in future versions of the
     * program. Please leave the original attribution information intact so
     * that credit and blame may be properly apportioned.</p>
     */
    public static double phase(double julian_date) {
        double date_within_epoch;
        double sun_eccent;
        double sun_mean_anomaly;
        double sun_perigree_co_ordinates_to_epoch;
        double sun_geocentric_elong;
        double moon_evection;
        double moon_variation;
        double moon_mean_anomaly;
        double moon_mean_longitude;
        double moon_annual_equation;
        double moon_correction_term1;
        double moon_correction_term2;
        double moon_correction_equation_of_center;
        double moon_corrected_anomaly;
        double moon_corrected_longitude;
        double moon_present_age;
        double moon_present_phase;
        double moon_present_longitude;

        /*
           Calculation of the Sun's position.
        */
        date_within_epoch = julian_date - EPOCH;
        sun_mean_anomaly = FIXANGLE((360.0 / 365.2422) * date_within_epoch);
        sun_perigree_co_ordinates_to_epoch = FIXANGLE(sun_mean_anomaly + SUN_ELONG_EPOCH - SUN_ELONG_PERIGEE);
        sun_eccent = kepler(sun_perigree_co_ordinates_to_epoch);
        sun_eccent = Math.sqrt((1.0 + ECCENT_EARTH_ORBIT) / (1.0 - ECCENT_EARTH_ORBIT)) * Math.tan(sun_eccent / 2.0);
        sun_eccent = 2.0 * TODEG(atan(sun_eccent));
        sun_geocentric_elong = FIXANGLE(sun_eccent + SUN_ELONG_PERIGEE);
        /*
           Calculation of the Moon's position.
        */
        moon_mean_longitude = FIXANGLE(13.1763966 * date_within_epoch + MOON_MEAN_LONGITUDE_EPOCH);
        moon_mean_anomaly = FIXANGLE(moon_mean_longitude - 0.1114041 * date_within_epoch - MOON_MEAN_LONGITUDE_PERIGREE);
        moon_evection = 1.2739 * Math.sin(TORAD(2.0 * (moon_mean_longitude - sun_geocentric_elong) - moon_mean_anomaly));
        moon_annual_equation = 0.1858 * Math.sin(TORAD(sun_perigree_co_ordinates_to_epoch));
        moon_correction_term1 = 0.37 * Math.sin(TORAD(sun_perigree_co_ordinates_to_epoch));
        moon_corrected_anomaly = moon_mean_anomaly + moon_evection - moon_annual_equation - moon_correction_term1;
        moon_correction_equation_of_center = 6.2886 * Math.sin(TORAD(moon_corrected_anomaly));
        moon_correction_term2 = 0.214 * Math.sin(TORAD(2.0 * moon_corrected_anomaly));
        moon_corrected_longitude = moon_mean_longitude + moon_evection + moon_correction_equation_of_center
                - moon_annual_equation + moon_correction_term2;
        moon_variation = 0.6583 * Math.sin(TORAD(2.0 * (moon_corrected_longitude - sun_geocentric_elong)));

        // true longitude
        moon_present_longitude = moon_corrected_longitude + moon_variation;
        moon_present_age = moon_present_longitude - sun_geocentric_elong;
        moon_present_phase = 100.0 * ((1.0 - Math.cos(TORAD(moon_present_age))) / 2.0);

        if (0.0 < FIXANGLE(moon_present_age) - 180.0) {
            moon_present_phase = -moon_present_phase;
        }

        _moonAgeAsDays = SYNMONTH * (FIXANGLE(moon_present_age) / 360.0);

        return moon_present_phase;
    }

    static public double atan(double x) {
        double SQRT3 = 1.732050807568877294;
        boolean signChange = false;
        boolean Invert = false;
        int sp = 0;
        double x2, a;
        // check up the sign change
        if (x < 0.) {
            x = -x;
            signChange = true;
        }
        // check up the invertation
        if (x > 1.) {
            x = 1 / x;
            Invert = true;
        }
        // process shrinking the domain until x<PI/12
        while (x > Math.PI / 12) {
            sp++;
            a = x + SQRT3;
            a = 1 / a;
            x = x * SQRT3;
            x = x - 1;
            x = x * a;
        }
        // calculation core
        x2 = x * x;
        a = x2 + 1.4087812;
        a = 0.55913709 / a;
        a = a + 0.60310579;
        a = a - (x2 * 0.05160454);
        a = a * x;
        // process until sp=0
        while (sp > 0) {
            a = a + Math.PI / 6;
            sp--;
        }
        // invertation took place
        if (Invert)
            a = Math.PI / 2 - a;
        // sign change took place
        if (signChange)
            a = -a;
        //
        return a;
    }

//    public static void main(String args[]) {
//        int year = Calendar.getInstance().get(Calendar.YEAR);
//        Ekadash ekadash = calculate(year);
//        System.out.println(ekadash);
//    }

    public static Ekadash calculate(int year) {
        return calculate(year,TimeZone.getTimeZone("UTC"));
    }

    @Override
    public String toString() {
        return "Ekadash{" +
                "array=" + array +
                '}';
    }

    public static Ekadash calculate(int year, TimeZone timeZone) {
        Ekadash ekadash = new Ekadash(Calendar.getInstance());
        GregorianCalendar calendar = new GregorianCalendar(timeZone);
        calendar.setTimeZone(timeZone);
        calendar.get(Calendar.HOUR_OF_DAY);
        calendar.set(year, Calendar.JANUARY, 1, 0, 0, 0);
        ekadash.getPhase(calendar);
        moveToNextDay(calendar, ekadash);
        while (calendar.get(Calendar.YEAR) < year + 1) {
            int day = (int) ekadash.getMoonAge();
            int inc = nextDay(day);
            if (inc == -1) {
                while (true) {
                    int d = moveToNextDay(calendar, ekadash);
                    if (d == 0) {
                        inc = 0;
                        break;
                    }
                }
            }
            int day_number = moveToNextDay(inc, calendar, ekadash);
            Date d = new Date(calendar.getTime().getTime() + timeZone.getRawOffset());
            ekadash.add(new Fasting(getDayType(day_number),d,d.getTime()/1000));
        }
        return ekadash;
    }
    public void add(Fasting fasting) {
        array.add(fasting);
    }

    public static class Fasting {
        private static final int COMMON_DAY = -1;
        private static final int EKADASHI = 1;
        private static final int NEW_MOON = 2;
        private static final int FOOL_MOON = 3;
        int type = COMMON_DAY;
        Date date;
        long unixtime;

        public Fasting(int type, Date date, long unixtime) {
            this.type = type;
            this.date = date;
            this.unixtime = unixtime;
        }

        @Override
        public String toString() {
            return "Fasting{" +
                    "type=" + type +
                    ", date=" + date +
                    ", unixtime=" + unixtime +
                    '}';
        }
    }

    private static int nextDay(int day) {
        if (day == 0) return 11;

        if (day < 11) return 11 - day;
        else if (day < 15) return 15 - day;
        else if (day < 26) return 26 - day;
        else return -1;
    }

    public static int getDayType(int day) {
        switch (day) {
            case 11:
            case 26:
                return Fasting.EKADASHI;
            case 0:
                return Fasting.NEW_MOON;
            case 15:
                return Fasting.FOOL_MOON;
            default:
                return Fasting.COMMON_DAY;
        }
    }


    public static int moveToNextDay(Calendar calendar, Ekadash mp) {
        return moveToNextDay(1, calendar, mp);
    }

    public static int moveToNextDay(int _day, Calendar calendar, Ekadash mp) {
        int day = (int) mp.getMoonAge(calendar);
        if (_day == 0)
            return day;
        int endDay = day + _day;
        int increment = 1000;
        while (day <= endDay) {
            if (day == endDay) {
                return endDay;
            }
            int incrementValue = increment;
            if (endDay - day > 1) {
                incrementValue = (endDay - day) * EARTH_SECONDS_MOON_DAY;
                if (incrementValue > 100000)
                    incrementValue = 100000;
            }
            calendar.add(Calendar.SECOND, incrementValue);
            mp.getPhase(calendar);
            day = (int) mp.getMoonAge();
            if (day == 0)
                return 0;
        }
        return day;
    }
}
