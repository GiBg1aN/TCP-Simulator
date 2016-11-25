package components;

import static mainPackage.MyConstants.*;

public class MySegment {
    private SegmentType segmentType;
    private User user;

    public MySegment(SegmentType segmentType, User user) {
        this.segmentType = segmentType;
        this.user = user;
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
