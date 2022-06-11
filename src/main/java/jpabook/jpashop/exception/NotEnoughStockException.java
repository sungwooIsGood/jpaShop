package jpabook.jpashop.exception;

public class NotEnoughStockException extends RuntimeException {

//    Override 함
    public NotEnoughStockException() {
        super();
    }

    // 오류 메세지 넘기는 메소드
    public NotEnoughStockException(String message) {
        super(message);
    }

    // 근원적인 Exception Trace를 뽑아준다.
    public NotEnoughStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughStockException(Throwable cause) {
        super(cause);
    }

    protected NotEnoughStockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
