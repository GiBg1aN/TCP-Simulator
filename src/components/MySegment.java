package components;

import mainPackage.SegmentType;


public interface MySegment {
    public void solveSegment(double timestamp);

    public SegmentType getSegmentType();

    public User getUser();

    public int getSeq();
}
