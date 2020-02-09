package NoAscync;

// public final 로 생성해서 접근하는게 좋음
// enum은 변경 될 일 없음 변경되면 안됨
// 대문자
public enum DataType {
    IMG('I'), MSG('M'), CMD('C');

    public final char type;

    DataType(char type) {
        this.type = type;
    }
}
