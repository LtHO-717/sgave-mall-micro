package com.sgave.mall.sgavemallshopping.constant;

public class OrderConstant {
    /**
     * 0-待支付,1-已支付,2-已发货,3-已完成,4-已取消,5-退款中,6-已退款
     */
    public static final Short STATUS_INIT = 0;
    public static final Short STATUS_PAY = 1;
    public static final Short STATUS_SHIP = 2;
    public static final Short STATUS_FINISH = 3;
    public static final Short STATUS_CANCEL = 4;
    public static final Short STATUS_REFUNDING = 5;
    public static final Short STATUS_REFUNDED = 6;




    public static final Integer ORDER_UNKNOWN = 720;
    public static final Integer ORDER_INVALID = 721;
    public static final Integer ORDER_INVALID_OPERATION = 725;
    public static final Integer ORDER_DELETE_FAILED = 623;
    public static final Integer ORDER_CONFIRM_NOT_ALLOWED = 620;

}
