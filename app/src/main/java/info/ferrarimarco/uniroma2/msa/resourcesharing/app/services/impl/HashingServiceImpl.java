package info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.impl;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;

public class HashingServiceImpl {

    private MessageDigest mda;

    private static final String tag = HashingServiceImpl.class.toString();

    @Inject
    public HashingServiceImpl(Context context) {

        Resources res = context.getResources();
        String hashingAlgorithm = res.getString(R.string.security_hashing_algorithm);
        String cryptoProvider = res.getString(R.string.security_provider);

        try {
            mda = MessageDigest.getInstance(hashingAlgorithm, cryptoProvider);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            Log.e(tag, "HashingServiceImpl failed initialisation - " + e.toString());
        }
    }

    public byte[] hash(String input) {
        return mda.digest(input.getBytes());
    }
}
