package com.bc.amazon.response;

import java.util.ArrayList;
import java.util.List;


public class OfferFull {

    private List<OfferItem> items = new ArrayList<OfferItem>();

    public OfferFull(com.amazon.xml.AWSECommerceService.ItemLookupResponse response) {
        com.amazon.xml.AWSECommerceService.Items[] itemsArray = response.getItems();
        for (int i = 0; i < itemsArray.length; i++){
            com.amazon.xml.AWSECommerceService.Items awsItems = itemsArray[i];
            com.amazon.xml.AWSECommerceService.Item[] itemArray = awsItems.getItem();
            if (itemArray != null){
                for (int j = 0; j < itemArray.length; j++){
                    com.amazon.xml.AWSECommerceService.Item item = itemArray[j];
                    items.add(new OfferItem(item));
                }
            }
        }
    }

    public class OfferItem {

        private String asin;
        private OfferSummary offerSummary;
        private Integer totalOffers = 0;
        private Integer totalOfferPages = 0;
        private List<Offer> offers = new ArrayList<Offer>();

        public OfferItem(com.amazon.xml.AWSECommerceService.Item item){
            asin = item.getASIN();
            offerSummary = new OfferSummary(item.getOfferSummary());
            if (item.getOffers() != null){
                if (item.getOffers().getTotalOfferPages() != null){
                    totalOfferPages = item.getOffers().getTotalOfferPages().intValue();
                }
                if (item.getOffers().getTotalOffers() != null){
                    totalOffers = item.getOffers().getTotalOffers().intValue();
                }
                com.amazon.xml.AWSECommerceService.Offer[] awsOffers = item.getOffers().getOffer();
                if (awsOffers != null){
                    for (int i = 0; i < awsOffers.length; i++){
                        offers.add(new Offer(awsOffers[i]));
                    }
                }
            }
        }

        public String getAsin() {
            return asin;
        }
        public void setAsin(String asin) {
            this.asin = asin;
        }
        public List<Offer> getOffers() {
            return offers;
        }
        public void setOffers(List<Offer> offers) {
            this.offers = offers;
        }
        public OfferSummary getOfferSummary() {
            return offerSummary;
        }
        public void setOfferSummary(OfferSummary offerSummary) {
            this.offerSummary = offerSummary;
        }
        public Integer getTotalOfferPages() {
            return totalOfferPages;
        }
        public void setTotalOfferPages(Integer totalOfferPages) {
            this.totalOfferPages = totalOfferPages;
        }
        public Integer getTotalOffers() {
            return totalOffers;
        }
        public void setTotalOffers(Integer totalOffers) {
            this.totalOffers = totalOffers;
        }


    }

    public class OfferSummary {
        private Integer lowestNewPriceAmmount = 0;
        private String lowestNewPriceCurrencyCode = "USD";
        private String lowestNewPriceFormatted = "$0.00";
        private Integer lowestUsedPriceAmmount = 0;
        private String lowestUsedPriceCurrencyCode = "USD";
        private String lowestUsedPriceFormatted = "$0.00";
        private Integer lowestCollectiblePriceAmmount = 0;
        private String lowestCollectiblePriceCurrencyCode = "USD";
        private String lowestCollectiblePriceFormatted = "$0.00";
        private Integer lowestRefurbishedPriceAmmount = 0;
        private String lowestRefurbishedPriceCurrencyCode = "USD";
        private String lowestRefurbishedPriceFormatted = "$0.00";
        private String totalNew = "0";
        private String totalUsed = "0";
        private String totalCollectible = "0";
        private String totalRefurbished = "0";

        public OfferSummary(com.amazon.xml.AWSECommerceService.OfferSummary os){
            if (os == null) return; // no offer summary
            totalNew = os.getTotalNew();
            totalUsed = os.getTotalUsed();
            totalCollectible = os.getTotalCollectible();
            totalRefurbished = os.getTotalRefurbished();
            if (os.getLowestNewPrice() != null){
                if (os.getLowestNewPrice().getAmount() != null){
                    lowestNewPriceAmmount = os.getLowestNewPrice().getAmount().intValue();
                }
                lowestNewPriceCurrencyCode = os.getLowestNewPrice().getCurrencyCode();
                lowestNewPriceFormatted = os.getLowestNewPrice().getFormattedPrice();
            }
            if (os.getLowestUsedPrice() != null){
                if (os.getLowestUsedPrice().getAmount() != null){
                    lowestUsedPriceAmmount = os.getLowestUsedPrice().getAmount().intValue();
                }
                lowestUsedPriceCurrencyCode = os.getLowestUsedPrice().getCurrencyCode();
                lowestUsedPriceFormatted = os.getLowestUsedPrice().getFormattedPrice();
            }
            if (os.getLowestCollectiblePrice() != null){
                if (os.getLowestCollectiblePrice().getAmount() != null){
                    lowestCollectiblePriceAmmount = os.getLowestCollectiblePrice().getAmount().intValue();
                }
                lowestCollectiblePriceCurrencyCode = os.getLowestCollectiblePrice().getCurrencyCode();
                lowestCollectiblePriceFormatted = os.getLowestCollectiblePrice().getFormattedPrice();
            }
            if (os.getLowestRefurbishedPrice() != null){
                if (os.getLowestRefurbishedPrice().getAmount() != null){
                    lowestRefurbishedPriceAmmount = os.getLowestRefurbishedPrice().getAmount().intValue();
                }
                lowestRefurbishedPriceCurrencyCode = os.getLowestRefurbishedPrice().getCurrencyCode();
                lowestRefurbishedPriceFormatted = os.getLowestRefurbishedPrice().getFormattedPrice();
            }
        }

        public Integer getLowestCollectiblePriceAmmount() {
            return lowestCollectiblePriceAmmount;
        }

        public void setLowestCollectiblePriceAmmount(
                Integer lowestCollectiblePriceAmmount) {
            this.lowestCollectiblePriceAmmount = lowestCollectiblePriceAmmount;
        }

        public String getLowestCollectiblePriceCurrencyCode() {
            return lowestCollectiblePriceCurrencyCode;
        }

        public void setLowestCollectiblePriceCurrencyCode(
                String lowestCollectiblePriceCurrencyCode) {
            this.lowestCollectiblePriceCurrencyCode = lowestCollectiblePriceCurrencyCode;
        }

        public String getLowestCollectiblePriceFormatted() {
            return lowestCollectiblePriceFormatted;
        }

        public void setLowestCollectiblePriceFormatted(
                String lowestCollectiblePriceFormatted) {
            this.lowestCollectiblePriceFormatted = lowestCollectiblePriceFormatted;
        }

        public Integer getLowestNewPriceAmmount() {
            return lowestNewPriceAmmount;
        }

        public void setLowestNewPriceAmmount(Integer lowestNewPriceAmmount) {
            this.lowestNewPriceAmmount = lowestNewPriceAmmount;
        }

        public String getLowestNewPriceCurrencyCode() {
            return lowestNewPriceCurrencyCode;
        }

        public void setLowestNewPriceCurrencyCode(String lowestNewPriceCurrencyCode) {
            this.lowestNewPriceCurrencyCode = lowestNewPriceCurrencyCode;
        }

        public String getLowestNewPriceFormatted() {
            return lowestNewPriceFormatted;
        }

        public void setLowestNewPriceFormatted(String lowestNewPriceFormatted) {
            this.lowestNewPriceFormatted = lowestNewPriceFormatted;
        }

        public Integer getLowestRefurbishedPriceAmmount() {
            return lowestRefurbishedPriceAmmount;
        }

        public void setLowestRefurbishedPriceAmmount(
                Integer lowestRefurbishedPriceAmmount) {
            this.lowestRefurbishedPriceAmmount = lowestRefurbishedPriceAmmount;
        }

        public String getLowestRefurbishedPriceCurrencyCode() {
            return lowestRefurbishedPriceCurrencyCode;
        }

        public void setLowestRefurbishedPriceCurrencyCode(
                String lowestRefurbishedPriceCurrencyCode) {
            this.lowestRefurbishedPriceCurrencyCode = lowestRefurbishedPriceCurrencyCode;
        }

        public String getLowestRefurbishedPriceFormatted() {
            return lowestRefurbishedPriceFormatted;
        }

        public void setLowestRefurbishedPriceFormatted(
                String lowestRefurbishedPriceFormatted) {
            this.lowestRefurbishedPriceFormatted = lowestRefurbishedPriceFormatted;
        }

        public Integer getLowestUsedPriceAmmount() {
            return lowestUsedPriceAmmount;
        }

        public void setLowestUsedPriceAmmount(Integer lowestUsedPriceAmmount) {
            this.lowestUsedPriceAmmount = lowestUsedPriceAmmount;
        }

        public String getLowestUsedPriceCurrencyCode() {
            return lowestUsedPriceCurrencyCode;
        }

        public void setLowestUsedPriceCurrencyCode(String lowestUsedPriceCurrencyCode) {
            this.lowestUsedPriceCurrencyCode = lowestUsedPriceCurrencyCode;
        }

        public String getLowestUsedPriceFormatted() {
            return lowestUsedPriceFormatted;
        }

        public void setLowestUsedPriceFormatted(String lowestUsedPriceFormatted) {
            this.lowestUsedPriceFormatted = lowestUsedPriceFormatted;
        }

        public String getTotalCollectible() {
            return totalCollectible;
        }

        public void setTotalCollectible(String totalCollectible) {
            this.totalCollectible = totalCollectible;
        }

        public String getTotalNew() {
            return totalNew;
        }

        public void setTotalNew(String totalNew) {
            this.totalNew = totalNew;
        }

        public String getTotalRefurbished() {
            return totalRefurbished;
        }

        public void setTotalRefurbished(String totalRefurbished) {
            this.totalRefurbished = totalRefurbished;
        }

        public String getTotalUsed() {
            return totalUsed;
        }

        public void setTotalUsed(String totalUsed) {
            this.totalUsed = totalUsed;
        }


    }

    public class Offer {

        private Merchant merchant;
        private OfferAttributes offerAttributes;
        private OfferListing offerListing;


        public Offer(com.amazon.xml.AWSECommerceService.Offer offer){
            if (offer == null){
                return;
            }
            merchant = new Merchant(offer.getMerchant());
            offerAttributes = new OfferAttributes(offer.getOfferAttributes());
            offerListing = new OfferListing(offer.getOfferListing(0));
        }

        public class Merchant {
            private String name;

            public Merchant(com.amazon.xml.AWSECommerceService.Merchant merchant){
                if (merchant == null){
                    return;
                }
                name = merchant.getName();
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }



        }

        public class OfferAttributes {
            private String condition;

            public OfferAttributes(com.amazon.xml.AWSECommerceService.OfferAttributes oa){
                if (oa == null){
                    return;
                }
                condition = oa.getCondition();
            }

            public String getCondition() {
                return condition;
            }

            public void setCondition(String condition) {
                this.condition = condition;
            }

        }

        public class OfferListing {
            private String offerListingId;
            private String availability;
            private Integer ammount = 0;
            private String currencyCode = "USD";
            private String formattedPrice = "$0.00";

            public OfferListing(com.amazon.xml.AWSECommerceService.OfferListing ol){
                if (ol == null){
                    return;
                }
                offerListingId = ol.getOfferListingId();
                availability = ol.getAvailability();
                if (ol.getPrice() != null){
                    if (ol.getPrice().getAmount() != null){
                        ammount = ol.getPrice().getAmount().intValue();
                    }
                    currencyCode = ol.getPrice().getCurrencyCode();
                    formattedPrice = ol.getPrice().getFormattedPrice();
                }
            }

            public Integer getAmmount() {
                return ammount;
            }

            public void setAmmount(Integer ammount) {
                this.ammount = ammount;
            }

            public String getAvailability() {
                return availability;
            }

            public void setAvailability(String availability) {
                this.availability = availability;
            }

            public String getCurrencyCode() {
                return currencyCode;
            }

            public void setCurrencyCode(String currencyCode) {
                this.currencyCode = currencyCode;
            }
            
            public String getFormattedPrice() {
                return formattedPrice;
            }

            public void setFormattedPrice(String formattedPrice) {
                this.formattedPrice = formattedPrice;
            }

            public String getOfferListingId() {
                return offerListingId;
            }

            public void setOfferListingId(String offerListingId) {
                this.offerListingId = offerListingId;
            }

        }

        public OfferAttributes getOfferAttributes() {
            return offerAttributes;
        }

        public void setOfferAttributes(OfferAttributes offerAttributes) {
            this.offerAttributes = offerAttributes;
        }

        public OfferListing getOfferListing() {
            return offerListing;
        }

        public void setOfferListing(OfferListing offerListing) {
            this.offerListing = offerListing;
        }

        public Merchant getMerchant() {
            return merchant;
        }

        public void setMerchant(Merchant merchant) {
            this.merchant = merchant;
        }
    }

    public List<OfferItem> getItems() {
        return items;
    }

    public void setItems(List<OfferItem> items) {
        this.items = items;
    }


}
