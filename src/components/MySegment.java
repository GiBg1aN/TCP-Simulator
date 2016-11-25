package components;

import static mainPackage.MyConstants.*;

public class MySegment {
    private SegmentType segmentType;
    private User user;
    private int seq;

    public MySegment(SegmentType segmentType, User user, int seq) {
        this.segmentType = segmentType;
        this.user = user;
        this.seq = seq;
    }

    public int getSeq() {
        return seq;
    }

    public User getUser() {
        return user;
    }
    
    public SegmentType getSegmentType() {
        return segmentType;
    }

    public void setSegmentType(SegmentType segmentType) {
        this.segmentType = segmentType;
    }
}
