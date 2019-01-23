package com.lelern.print;

public class DataBean {
        private String cCode;

    public String getDdate() {
        return ddate;
    }

    public void setDdate(String ddate) {
        this.ddate = ddate;
    }

    private String ddate;
        private String mocode;
        private String invstd;
        private String invname;
        private int beginsn;
        private int endsn;
        private int sncount;
        public void setCCode(String cCode) {
            this.cCode = cCode;
        }
        public String getCCode() {
            return cCode;
        }



        public void setMocode(String mocode) {
            this.mocode = mocode;
        }
        public String getMocode() {
            return mocode;
        }

        public void setInvstd(String invstd) {
            this.invstd = invstd;
        }
        public String getInvstd() {
            return invstd;
        }

        public void setInvname(String invname) {
            this.invname = invname;
        }
        public String getInvname() {
            return invname;
        }

        public void setBeginsn(int beginsn) {
            this.beginsn = beginsn;
        }
        public int getBeginsn() {
            return beginsn;
        }

        public void setEndsn(int endsn) {
            this.endsn = endsn;
        }
        public int getEndsn() {
            return endsn;
        }

        public void setSncount(int sncount) {
            this.sncount = sncount;
        }
        public int getSncount() {
            return sncount;
        }


}
