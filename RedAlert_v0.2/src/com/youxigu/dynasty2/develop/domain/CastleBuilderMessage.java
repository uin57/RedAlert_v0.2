package com.youxigu.dynasty2.develop.domain;

import java.io.Serializable;
import java.util.List;

import com.google.protobuf.Message;
import com.youxigu.dynasty2.chat.EventMessage;
import com.youxigu.dynasty2.chat.ISendMessage;
import com.youxigu.dynasty2.chat.proto.CommonHead;
import com.youxigu.dynasty2.develop.proto.DevelopMsg;

public class CastleBuilderMessage implements ISendMessage, Serializable{
    private static final long serialVersionUID = -6366950021527237520L;
    private List<CastleBuilder> builders;

    public List<CastleBuilder> getBuilders() {
        return builders;
    }

    public void setBuilders(List<CastleBuilder> builders) {
        this.builders = builders;
    }

    public CastleBuilderMessage(){}

    public CastleBuilderMessage(List<CastleBuilder> builders){
        this.builders = builders;
    }

    @Override
    public Message build() {
        DevelopMsg.BuilderSendEvent.Builder sEvent = DevelopMsg.BuilderSendEvent.newBuilder();
        CommonHead.ResponseHead.Builder headBr = CommonHead.ResponseHead.newBuilder();
        headBr.setCmd(EventMessage.TYPE_CASTLE_BUILDER_CHANGED);
        headBr.setRequestCmd(EventMessage.TYPE_CASTLE_BUILDER_CHANGED);
        sEvent.setResponseHead(headBr.build());

        for (CastleBuilder builder : builders) {
            DevelopMsg.BuilderInfo bi = builder.toMsg();
            sEvent.addBuilderInfoList(bi);
        }
       return sEvent.build();
    }
}
