package com.hong.utils;

public interface MsgCallBack {

    //�����������Ƿ�ɹ�
    void onCreateServer(boolean flag);

    //���пͻ�����������
    void onAccepted();

    //���������ɹ�����ʧ��
    void onConntextEvent(boolean flag);

    //���ܿͷ�����Ϣ
    void onReveived(byte[] buff);

    //�������ݷ����쳣
    void onReveiveFailed();

    void onReceived(byte[] buff, int count, String socketID);
}
