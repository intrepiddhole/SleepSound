/*
 * Copyright 2012-2014 One Platform Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onepf.oms.appstore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.onepf.oms.AppstoreInAppBillingService;
import org.onepf.oms.OpenIabHelper;
import org.onepf.oms.SkuManager;
import org.onepf.oms.appstore.googleUtils.IabException;
import org.onepf.oms.appstore.googleUtils.IabHelper;
import org.onepf.oms.appstore.googleUtils.IabHelper.OnIabPurchaseFinishedListener;
import org.onepf.oms.appstore.googleUtils.IabHelper.OnIabSetupFinishedListener;
import org.onepf.oms.appstore.googleUtils.IabResult;
import org.onepf.oms.appstore.googleUtils.Inventory;
import org.onepf.oms.appstore.googleUtils.Purchase;
import org.onepf.oms.appstore.googleUtils.SkuDetails;
import org.onepf.oms.util.CollectionUtils;
import org.onepf.oms.util.Logger;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.sec.android.iap.IAPConnector;

/**
 * @author Ruslan Sayfutdinov
 * @since 10.10.2013
 */

public class SamsungAppsBillingService implements AppstoreInAppBillingService {
    private static final int ITEM_RESPONSE_COUNT = 100;

    // IAP Modes are used for IAPConnector.init() 
    public static final int IAP_MODE_COMMERCIAL = 0;
    public static final int IAP_MODE_TEST_SUCCESS = 1;
    public static final int IAP_MODE_TEST_FAIL = -1;
    private static final int CURRENT_MODE = SamsungApps.isSamsungTestMode ? IAP_MODE_TEST_SUCCESS : IAP_MODE_COMMERCIAL;

    public static final String IAP_SERVICE_NAME = "com.sec.android.iap.service.iapService";
    public static final String ACCOUNT_ACTIVITY_NAME = "com.sec.android.iap.activity.AccountActivity";
    public static final String PAYMENT_ACTIVITY_NAME = "com.sec.android.iap.activity.PaymentMethodListActivity";
    // ========================================================================
    // BILLING RESPONSE CODE
    // ========================================================================
    public static final int IAP_RESPONSE_RESULT_OK = 0;
    public static final int IAP_RESPONSE_RESULT_UNAVAILABLE = 2;
    // ========================================================================
    public static final int FLAG_INCLUDE_STOPPED_PACKAGES = 32;
    // ========================================================================
    // BUNDLE KEY
    // ========================================================================
    public static final String KEY_NAME_THIRD_PARTY_NAME = "THIRD_PARTY_NAME";
    public static final String KEY_NAME_STATUS_CODE = "STATUS_CODE";
    public static final String KEY_NAME_ERROR_STRING = "ERROR_STRING";
    public static final String KEY_NAME_IAP_UPGRADE_URL = "IAP_UPGRADE_URL";
    public static final String KEY_NAME_ITEM_GROUP_ID = "ITEM_GROUP_ID";
    public static final String KEY_NAME_ITEM_ID = "ITEM_ID";
    public static final String KEY_NAME_RESULT_LIST = "RESULT_LIST";
    public static final String KEY_NAME_RESULT_OBJECT = "RESULT_OBJECT";
    // ========================================================================
    // ITEM JSON KEY
    // ========================================================================
    public static final String JSON_KEY_ITEM_ID = "mItemId";
    public static final String JSON_KEY_ITEM_NAME = "mItemName";
    public static final String JSON_KEY_ITEM_DESC = "mItemDesc";
    public static final String JSON_KEY_ITEM_PRICE = "mItemPrice";
    public static final String JSON_KEY_CURRENCY_UNIT = "mCurrencyUnit";
    public static final String JSON_KEY_ITEM_IMAGE_URL = "mItemImageUrl";
    public static final String JSON_KEY_ITEM_DOWNLOAD_URL = "mItemDownloadUrl";
    public static final String JSON_KEY_PURCHASE_DATE = "mPurchaseDate";
    public static final String JSON_KEY_PAYMENT_ID = "mPaymentId";
    public static final String JSON_KEY_PURCHASE_ID = "mPurchaseId";
    public static final String JSON_KEY_TYPE = "mType";
    public static final String JSON_KEY_ITEM_PRICE_STRING = "mItemPriceString";
    // ========================================================================
    // ITEM TYPE
    // ========================================================================
    public static final String ITEM_TYPE_CONSUMABLE = "00";
    public static final String ITEM_TYPE_NON_CONSUMABLE = "01";
    public static final String ITEM_TYPE_SUBSCRIPTION = "02";
    public static final String ITEM_TYPE_ALL = "10";

    // ========================================================================
    // define request code for IAPService.
    // ========================================================================
    public static final int REQUEST_CODE_IS_IAP_PAYMENT = 1;
    public static final int REQUEST_CODE_IS_ACCOUNT_CERTIFICATION = 899;

    // ========================================================================
    // define status code passed to 3rd party application
    // ========================================================================
    public static final int IAP_ERROR_NONE = 0;
    public static final int IAP_PAYMENT_IS_CANCELED = 1;
    public static final int IAP_ERROR_INITIALIZATION = -1000;
    public static final int IAP_ERROR_NEED_APP_UPGRADE = -1001;
    public static final int IAP_ERROR_COMMON = -1002;
    public static final int IAP_ERROR_ALREADY_PURCHASED = -1003;
    public static final int IAP_ERROR_WHILE_RUNNING = -1004;
    public static final int IAP_ERROR_PRODUCT_DOES_NOT_EXIST = -1005;
    public static final int IAP_ERROR_CONFIRM_INBOX = -1006;
    // ========================================================================

    private volatile boolean isBound;
    @Nullable
    private IAPConnector mIapConnector;
    private Activity activity;
    private OpenIabHelper.Options options;
    @Nullable
    private ServiceConnection serviceConnection;
    private String purchasingItemType;

    @Nullable
    private OnIabSetupFinishedListener setupListener = null;
    // The listener registered on launchPurchaseFlow, which we have to call back when
    // the purchase finishes
    @Nullable
    private OnIabPurchaseFinishedListener mPurchaseListener = null;
    private int mRequestCode;
    private String mItemGroupId;
    private String mExtraData;

    public SamsungAppsBillingService(Activity context, OpenIabHelper.Options options) {
        this.activity = context;
        this.options = options;
    }

    @Override
    public void startSetup(final OnIabSetupFinishedListener listener) {
        this.setupListener = listener;

        ComponentName com = new ComponentName(SamsungApps.IAP_PACKAGE_NAME, ACCOUNT_ACTIVITY_NAME);
        Intent intent = new Intent();
        intent.setComponent(com);
        activity.startActivityForResult(intent, options.getSamsungCertificationRequestCode());
    }

    @Override
    public Inventory queryInventory(boolean querySkuDetails, @Nullable List<String> moreItemSkus, @Nullable List<String> moreSubsSkus) throws IabException {
        Inventory inventory = new Inventory();

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String today = simpleDateFormat.format(date);

        /* Get all itemGroupIds from existing skus */
        Set<String> itemGroupIds = new HashSet<String>();
        final List<String> allStoreSkus = SkuManager.getInstance().getAllStoreSkus(OpenIabHelper.NAME_SAMSUNG);
        if (!CollectionUtils.isEmpty(allStoreSkus)) {
            for (String sku : allStoreSkus) {
                itemGroupIds.add(getItemGroupId(sku));
            }
        }

        /* Query getItemsInbox for each itemGroupId */
        for (String itemGroupId : itemGroupIds) {
            int startNum = 1;
            int endNum = ITEM_RESPONSE_COUNT;
            Bundle itemInbox;
            do {
                itemInbox = null;
                try {
                    Logger.d("getItemsInbox, startNum = ", startNum, ", endNum = ", endNum);
                    itemInbox = mIapConnector.getItemsInbox(activity.getPackageName(), itemGroupId, startNum, endNum, "19700101", today);
                } catch (RemoteException e) {
                    Logger.e("Samsung getItemsInbox: ", e);
                }
                startNum += ITEM_RESPONSE_COUNT;
                endNum += ITEM_RESPONSE_COUNT;
            }
            while (processItemsBundle(itemInbox, itemGroupId, inventory, querySkuDetails, true, false, null));
        }
        if (querySkuDetails) {
            Set<String> queryItemGroupIds = new HashSet<String>();
            Set<String> queryItemIds = new HashSet<String>();
            if (moreItemSkus != null) {
                for (String sku : moreItemSkus) {
                    queryItemGroupIds.add(getItemGroupId(sku));
                    queryItemIds.add(getItemId(sku));
                }
            }
            if (moreSubsSkus != null) {
                for (String sku : moreSubsSkus) {
                    queryItemGroupIds.add(getItemGroupId(sku));
                    queryItemIds.add(getItemId(sku));
                }
            }
            if (!queryItemIds.isEmpty()) {
                for (String itemGroupId : queryItemGroupIds) {
                    int startNum = 1;
                    int endNum = ITEM_RESPONSE_COUNT;
                    Bundle itemList;
                    do {
                        itemList = null;
                        try {
                            itemList = mIapConnector.getItemList(CURRENT_MODE, activity.getPackageName(), itemGroupId, startNum, endNum, ITEM_TYPE_ALL);
                        } catch (RemoteException e) {
                            Logger.e("Samsung getItemList: ", e);
                        }
                        startNum += ITEM_RESPONSE_COUNT;
                        endNum += ITEM_RESPONSE_COUNT;
                    }
                    while (processItemsBundle(itemList, itemGroupId, inventory, querySkuDetails, false, true, queryItemIds));
                }
            }
        }
        return inventory;
    }

    @Override
    public void launchPurchaseFlow(@NotNull Activity activity, @NotNull String sku, String itemType, int requestCode, OnIabPurchaseFinishedListener listener, String extraData) {
        String itemGroupId = getItemGroupId(sku);
        String itemId = getItemId(sku);

        Bundle bundle = new Bundle();
        bundle.putString(KEY_NAME_THIRD_PARTY_NAME, activity.getPackageName());
        bundle.putString(KEY_NAME_ITEM_GROUP_ID, itemGroupId);
        bundle.putString(KEY_NAME_ITEM_ID, itemId);
        Logger.d("launchPurchase: itemGroupId = ", itemGroupId, ", itemId = ", itemId);
        ComponentName cmpName = new ComponentName(SamsungApps.IAP_PACKAGE_NAME, PAYMENT_ACTIVITY_NAME);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(cmpName);
        intent.putExtras(bundle);
        mRequestCode = requestCode;
        mPurchaseListener = listener;
        purchasingItemType = itemType;
        mItemGroupId = itemGroupId;
        mExtraData = extraData;
        Logger.d("Request code: ", requestCode);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == options.getSamsungCertificationRequestCode()) {
            if (resultCode == Activity.RESULT_OK) {
                bindIapService();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                setupListener.onIabSetupFinished(new IabResult(IabHelper.BILLING_RESPONSE_RESULT_USER_CANCELED,
                        "Account certification canceled"));
            } else {
                setupListener.onIabSetupFinished(new IabResult(IabHelper.BILLING_RESPONSE_RESULT_ERROR,
                        "Unknown error. Result code: " + resultCode));
            }
            return true;
        }
        if (requestCode != mRequestCode) {
            return false;
        }
        int errorCode = IabHelper.BILLING_RESPONSE_RESULT_ERROR;
        String errorMsg = "Unknown error";
        Purchase purchase = new Purchase(OpenIabHelper.NAME_SAMSUNG);
        if (data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                int statusCode = extras.getInt(KEY_NAME_STATUS_CODE);
                errorMsg = extras.getString(KEY_NAME_ERROR_STRING);
                String itemId = extras.getString(KEY_NAME_ITEM_ID);
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        switch (statusCode) {
                            case IAP_ERROR_NONE:
                                errorCode = IabHelper.BILLING_RESPONSE_RESULT_OK;
                                break;
                            case IAP_ERROR_ALREADY_PURCHASED:
                                errorCode = IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED;
                                break;
                            case IAP_ERROR_PRODUCT_DOES_NOT_EXIST:
                                errorCode = IabHelper.BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE;
                                break;
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        errorCode = IabHelper.BILLING_RESPONSE_RESULT_USER_CANCELED;
                        break;
                }
                String purchaseData = extras.getString(KEY_NAME_RESULT_OBJECT);
                try {
                    JSONObject purchaseJson = new JSONObject(purchaseData);

                    purchase.setOriginalJson(purchaseData);
                    purchase.setOrderId(purchaseJson.getString(JSON_KEY_PAYMENT_ID));
                    purchase.setPurchaseTime(Long.parseLong(purchaseJson.getString(JSON_KEY_PURCHASE_DATE)));
                    purchase.setToken(purchaseJson.getString(JSON_KEY_PURCHASE_ID));
                } catch (JSONException e) {
                    Logger.e("JSON parse error: ", e);
                }

                purchase.setItemType(purchasingItemType);
                purchase.setSku(SkuManager.getInstance().getSku(OpenIabHelper.NAME_SAMSUNG, mItemGroupId + '/' + itemId));
                purchase.setPackageName(activity.getPackageName());
                purchase.setPurchaseState(0);
                purchase.setDeveloperPayload(mExtraData);
            }
        }
        Logger.d("Samsung result code: ", errorCode, ", msg: ", errorMsg);
        mPurchaseListener.onIabPurchaseFinished(new IabResult(errorCode, errorMsg), purchase);
        return true;
    }


    @Override
    public void consume(Purchase itemInfo) throws IabException {
        // Nothing to do here
    }

    @Override
    public boolean subscriptionsSupported() {
        return true;
    }

    @Override
    public void dispose() {
        if (serviceConnection != null && isBound) {
            activity.getApplicationContext().unbindService(serviceConnection);
            isBound = false;
        }
        serviceConnection = null;
        mIapConnector = null;
    }

    private String getItemGroupId(@NotNull String sku) {
        SamsungApps.checkSku(sku);
        String[] skuParts = sku.split("/");
        return skuParts[0];
    }

    private String getItemId(@NotNull String sku) {
        SamsungApps.checkSku(sku);
        String[] skuParts = sku.split("/");
        return skuParts[1];
    }

    private void bindIapService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mIapConnector = IAPConnector.Stub.asInterface(service);
                if (mIapConnector != null) {
                    initIap();
                } else {
                    setupListener.onIabSetupFinished(new IabResult(IabHelper.BILLING_RESPONSE_RESULT_ERROR,
                            "IAP service bind failed"));
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };

        final Intent implicitIntent = new Intent(IAP_SERVICE_NAME);
        final List<ResolveInfo> infoList = activity.getPackageManager().queryIntentServices(implicitIntent, 0);
        if (!CollectionUtils.isEmpty(infoList)) {
            final ResolveInfo serviceInfo = infoList.get(0);
            final String packageName = serviceInfo.serviceInfo.packageName;
            final String className = serviceInfo.serviceInfo.name;
            final ComponentName component = new ComponentName(packageName, className);
            final Intent serviceIntent = new Intent(implicitIntent);
            serviceIntent.setComponent(component);
            isBound = activity.getApplicationContext().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            isBound = false;
        }
    }


    private void initIap() {
        int errorCode = IabHelper.BILLING_RESPONSE_RESULT_ERROR;
        String errorMsg = "Init IAP service failed";
        try {
            Bundle result = mIapConnector.init(CURRENT_MODE);
            if (result != null) {
                int statusCode = result.getInt(KEY_NAME_STATUS_CODE);
                Logger.d("Init IAP connection status code: ", statusCode);
                errorMsg = result.getString(KEY_NAME_ERROR_STRING);
                if (statusCode == IAP_ERROR_NONE) {
                    errorCode = IabHelper.BILLING_RESPONSE_RESULT_OK;
                }
            }
        } catch (RemoteException e) {
            Logger.e("Init IAP: ", e);
        }
        setupListener.onIabSetupFinished(new IabResult(errorCode, errorMsg));
    }

    private boolean processItemsBundle(@Nullable Bundle itemsBundle, String itemGroupId, @NotNull Inventory inventory, boolean querySkuDetails, boolean addPurchase, boolean addConsumable, @Nullable Set<String> queryItemIds) {
        if (itemsBundle == null || itemsBundle.getInt(KEY_NAME_STATUS_CODE) != IAP_ERROR_NONE) {
            return false;
        }

        ArrayList<String> nameResults = itemsBundle.getStringArrayList(KEY_NAME_RESULT_LIST);
        for (String nameResult : nameResults) {
            try {
                JSONObject item = new JSONObject(nameResult);
                String itemId = item.getString(JSON_KEY_ITEM_ID);
                if (queryItemIds == null || queryItemIds.contains(itemId)) {
                    String rawType = item.getString(JSON_KEY_TYPE);
                    // Do not add consumable item into inventory
                    if (rawType.equals(ITEM_TYPE_CONSUMABLE) && !addConsumable) {
                        continue;
                    }
                    String itemType = rawType.equals(ITEM_TYPE_SUBSCRIPTION) ? IabHelper.ITEM_TYPE_SUBS : IabHelper.ITEM_TYPE_INAPP;

                    if (addPurchase) {
                        Purchase purchase = new Purchase(OpenIabHelper.NAME_SAMSUNG);
                        purchase.setItemType(itemType);
                        purchase.setSku(SkuManager.getInstance()
                                .getSku(OpenIabHelper.NAME_SAMSUNG, itemGroupId + '/' + itemId));
                        purchase.setPackageName(activity.getPackageName());
                        purchase.setPurchaseState(0);
                        purchase.setDeveloperPayload("");

                        purchase.setOrderId(item.getString(JSON_KEY_PAYMENT_ID));
                        purchase.setPurchaseTime(Long.parseLong(item.getString(JSON_KEY_PURCHASE_DATE)));
                        purchase.setToken(item.getString(JSON_KEY_PURCHASE_ID));

                        inventory.addPurchase(purchase);
                    }
                    if (!addPurchase || querySkuDetails) {
                        String name = item.getString(JSON_KEY_ITEM_NAME);
                        String price = item.getString(JSON_KEY_ITEM_PRICE_STRING);
                        String desc = item.getString(JSON_KEY_ITEM_DESC);
                        inventory.addSkuDetails(new SkuDetails(itemType,
                                SkuManager.getInstance().getSku(OpenIabHelper.NAME_SAMSUNG, itemGroupId + '/' + itemId),
                                name, price, desc));
                    }
                }
            } catch (JSONException e) {
                Logger.e("JSON parse error", e);
            }
        }
        return nameResults.size() == ITEM_RESPONSE_COUNT;
    }

}