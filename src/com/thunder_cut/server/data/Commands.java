package com.thunder_cut.server.data;

import java.util.function.Consumer;

/**
 * op --> 서버권한 부여 서버에서 쓰는 명령어를 사용가능하게 됨 + 클라의 기능,커맨드는 계속 유지
 * ignore --> 특정 사람 차단 (Image + Message)
 * blind --> 내 화면 안보여주기
 * set_name --> 닉네임 설정
 * <p>
 * kick, op --> 서버의 권한
 */
public enum Commands {

    SET_NAME("/set_name"),
    KICK("/kick"),
    OP("/op"),
    IGNORE("/ignore"),
    BLIND("/blind");

    public final String command;

    Commands(String command) {
        this.command = command;
    }
    public static Commands getCommand(String type){
        for(Commands commands : Commands.values()){
            if(commands.command.equals(type)){
                return commands;
            }
        }
        return null;
    }
}
