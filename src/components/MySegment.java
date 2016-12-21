package components;

import mainPackage.MyConstants;


public interface MySegment {

    public void solveSegment(double timestamp);

    public int getSeq();

    public User getUser();

    public MyConstants.SegmentType getSegmentType();
}
