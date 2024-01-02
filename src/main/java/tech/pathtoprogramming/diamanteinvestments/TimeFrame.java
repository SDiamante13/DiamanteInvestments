package tech.pathtoprogramming.diamanteinvestments;

// Creating enum for TIME_SERIES selection
public enum TimeFrame {
    INTRADAY {
        public String toString() {
            return "TIME_SERIES_INTRADAY";
        }
    },
    DAILY {
        public String toString() {
            return "TIME_SERIES_DAILY";
        }
    },
    WEEKLY {
        public String toString() {
            return "TIME_SERIES_WEEKLY";
        }
    },
    MONTHLY {
        public String toString() {
            return "TIME_SERIES_MONTHLY";
        }
    }
}
