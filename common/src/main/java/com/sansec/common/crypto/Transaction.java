package com.sansec.common.crypto;


import com.sansec.common.exception.ValidationException;

/**
 * @author QuincySx
 * @date 2018/3/8 下午1:56
 */
public interface Transaction {
    byte[] sign(ECKeyPair key) throws ValidationException;

    public byte[] getSignBytes();

    /**
     * Eth 使用的方法
     *
     * @return
     */
    public byte[] getData();


}
