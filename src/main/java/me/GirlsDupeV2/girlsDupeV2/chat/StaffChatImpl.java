package me.GirlsDupeV2.girlsDupeV2.chat;

public class StaffChatImpl implements StaffChatType {

    @Override
    public String getCommand() {
        return "staffchat";
    }

    @Override
    public String getToggleCommand() {
        return "sctoggle";
    }

    @Override
    public String getLockCommand() {
        return "scdisable";
    }

    @Override
    public String getPrefix() {
        return "staffchat";
    }

    @Override
    public String getPermission() {
        return "staff.staffchat";
    }

    @Override
    public String getType() {
        return "staff";
    }
}