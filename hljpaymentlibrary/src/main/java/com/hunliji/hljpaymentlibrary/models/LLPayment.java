package com.hunliji.hljpaymentlibrary.models;

import com.hunliji.hljcommonlibrary.models.BankCard;

import java.util.ArrayList;
import java.util.List;

/**
 * 连连支付方式model
 * Created by wangtao on 2017/2/9.
 */

public class LLPayment extends Payment{

    private List<BankCard> cards; //已绑定的银行卡列表
    private BankCard currentCard; //选择使用的银行卡

    public LLPayment(String payAgent) {
        super(payAgent);
    }

    public void setCards(List<BankCard> cards) {
        this.cards = cards;
        if(currentCard==null&&cards!=null&&!cards.isEmpty()){
            currentCard=cards.get(0);
        }
    }

    public void setCurrentCard(BankCard currentCard) {
        this.currentCard = currentCard;
    }

    public List<BankCard> getCards() {
        if(cards==null){
            cards=new ArrayList<>();
        }
        return cards;
    }

    public BankCard getCurrentCard() {
        return currentCard;
    }
}
