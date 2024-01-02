package tech.pathtoprogramming.diamanteinvestments;

// Creating enum for Time interval (only applicable to INTRADAY)
public enum Interval {
    ONE{
        public String toString() {
            return "1min";
        }
    },
    FIVE{
        public String toString() {
            return "5min";
        }
    },
    FIFTEEN{
        public String toString() {
            return "15min";
        }
    },
    THIRTY{
        public String toString() {
            return "30min";
        }
    },
    SIXTY{
            public String toString() {
                return "60min";
            }
}
    }
