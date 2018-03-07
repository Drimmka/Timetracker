package timetracker.com;


/**
 * Class representing work record in database
 */

public class WorkEntry {

    private long checkinTime;
    private long checkoutTime;
    private long totalWorkTime;
    private String checkinTimeStr = "-";
    private String checkoutTimeStr = "-";
    private String totalWorkTimeStr = "-";
    private int id;

    public WorkEntry(long checkinTime, long checkoutTime, long totalWorkTime, int id) {

        setId(id);

        setCheckinTime(checkinTime);
        setCheckoutTime(checkoutTime);
        setTotalWorkTime(totalWorkTime);

        setCheckinTimeStr(checkinTime);
        setCheckoutTimeStr(checkoutTime);
        setTotalWorkTimeStr(totalWorkTime);
    }

    public long getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(long checkinTime) {
        this.checkinTime = checkinTime;
    }

    public long getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(long checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public long getTotalWorkTime() {
        return totalWorkTime;
    }

    public void setTotalWorkTime(long totalWorkTime) {
        this.totalWorkTime = totalWorkTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCheckinTimeStr() {
        return checkinTimeStr;
    }

    public void setCheckinTimeStr(long checkinTime) {
        if (checkinTime > 0){
            this.checkinTimeStr = StringFormatUtils.formatEntryTime(checkinTime, StringFormatUtils.FORMAT_TIME_ENTRY);
        }
    }

    public String getCheckoutTimeStr() {
        return checkoutTimeStr;
    }

    public void setCheckoutTimeStr(long checkoutTime) {
        if (checkoutTime > 0) {
            this.checkoutTimeStr = StringFormatUtils.formatEntryTime(checkoutTime, StringFormatUtils.FORMAT_TIME_ENTRY);
        }
    }

    public String getTotalWorkTimeStr() {
        return totalWorkTimeStr;
    }

    public void setTotalWorkTimeStr(long totalWorkTime) {
        if (totalWorkTime > 0){
            this.totalWorkTimeStr = StringFormatUtils.formatWorkDuration(totalWorkTime, StringFormatUtils.FORMAT_TOTAL_WORK_TIME);
        }

    }


}
