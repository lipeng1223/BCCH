package com.bc.amazon.response;

import java.math.BigInteger;

import com.amazon.xml.AWSECommerceService.Item;

public class Medium {

    private String detailPage;
    private String salesRank;
    private String asin;
    private OfferSummary offerSummary;
    private ItemAttributes itemAttributes;
    private Image smallImage;
    private Image mediumImage;
    private Image largeImage;

    public Medium(com.amazon.xml.AWSECommerceService.ItemLookupResponse response) {
        if (response.getItems(0) != null && response.getItems(0).getItem(0) != null){
            Item item = response.getItems(0).getItem(0);
            asin = item.getASIN();
            detailPage = item.getDetailPageURL();
            salesRank = item.getSalesRank();
            offerSummary = new OfferSummary(item.getOfferSummary());
            itemAttributes = new ItemAttributes(item.getItemAttributes());
            smallImage = new Image(item.getSmallImage());
            mediumImage = new Image(item.getMediumImage());
            largeImage = new Image(item.getLargeImage());
        }
    }

    public class Image {

        private String url;
        private Float height;
        private Float width;

        public Image(com.amazon.xml.AWSECommerceService.Image image) {
            if (image == null){
                return;
            }
            url = image.getURL();
            if (image.getHeight() != null)
                height = image.getHeight().get_value().floatValue();
            if (image.getWidth() != null)
                width = image.getWidth().get_value().floatValue();
        }

        public Float getHeight() {
            return height;
        }

        public void setHeight(Float height) {
            this.height = height;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Float getWidth() {
            return width;
        }

        public void setWidth(Float width) {
            this.width = width;
        }

    }

    public class ItemAttributes {

        private String author;
        private String binding;
        private String ean;
        private String isbn;
        private String listPrice;
        private Integer numberOfPages;
        private Float height;
        private Float length;
        private Float width;
        private Float weight;
        private String productGroup;
        private String publicationDate;
        private String publisher;
        private String title;

        public ItemAttributes(com.amazon.xml.AWSECommerceService.ItemAttributes ia){
            if (ia == null){
                return;
            }
            StringBuilder sb = new StringBuilder();
            if (ia.getAuthor() != null){
                for (String a : ia.getAuthor()){
                    if (author != null){
                        // add a comma
                        sb.append(", ");
                    }
                    sb.append(a);
                }
                author = sb.toString();
            }
            binding = ia.getBinding();
            ean = ia.getEAN();
            isbn = ia.getISBN();
            listPrice = ia.getListPrice().getFormattedPrice();
            numberOfPages = ia.getNumberOfPages().intValue();
            if (ia.getItemDimensions() != null){
                if (ia.getItemDimensions().getHeight() != null)
                    height = ia.getItemDimensions().getHeight().get_value().floatValue();
                if (ia.getItemDimensions().getLength() != null)
                    length = ia.getItemDimensions().getLength().get_value().floatValue();
                if (ia.getItemDimensions().getWidth() != null)
                    width = ia.getItemDimensions().getWidth().get_value().floatValue();
                if (ia.getItemDimensions().getWeight() != null)
                    weight = ia.getItemDimensions().getWeight().get_value().floatValue();
            }
            productGroup = ia.getProductGroup();
            publicationDate = ia.getPublicationDate();
            publisher = ia.getPublisher();
            title = ia.getTitle();
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getBinding() {
            return binding;
        }

        public void setBinding(String binding) {
            this.binding = binding;
        }

        public String getEan() {
            return ean;
        }

        public void setEan(String ean) {
            this.ean = ean;
        }

        public Float getHeight() {
            return height;
        }

        public void setHeight(Float height) {
            this.height = height;
        }

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public Float getLength() {
            return length;
        }

        public void setLength(Float length) {
            this.length = length;
        }

        public String getListPrice() {
            return listPrice;
        }

        public void setListPrice(String listPrice) {
            this.listPrice = listPrice;
        }

        public Integer getNumberOfPages() {
            return numberOfPages;
        }

        public void setNumberOfPages(Integer numberOfPages) {
            this.numberOfPages = numberOfPages;
        }

        public String getProductGroup() {
            return productGroup;
        }

        public void setProductGroup(String productGroup) {
            this.productGroup = productGroup;
        }

        public String getPublicationDate() {
            return publicationDate;
        }

        public void setPublicationDate(String publicationDate) {
            this.publicationDate = publicationDate;
        }

        public String getPublisher() {
            return publisher;
        }

        public void setPublisher(String publisher) {
            this.publisher = publisher;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Float getWeight() {
            return weight;
        }

        public void setWeight(Float weight) {
            this.weight = weight;
        }

        public Float getWidth() {
            return width;
        }

        public void setWidth(Float width) {
            this.width = width;
        }


    }

    public class OfferSummary {
        private BigInteger lowestNewPriceAmmount;
        private String lowestNewPriceCurrencyCode;
        private String lowestNewPriceFormatted = "$0";
        private BigInteger lowestUsedPriceAmmount;
        private String lowestUsedPriceCurrencyCode;
        private String lowestUsedPriceFormatted = "$0";
        private BigInteger lowestCollectiblePriceAmmount;
        private String lowestCollectiblePriceCurrencyCode;
        private String lowestCollectiblePriceFormatted = "$0";
        private BigInteger lowestRefurbishedPriceAmmount;
        private String lowestRefurbishedPriceCurrencyCode;
        private String lowestRefurbishedPriceFormatted = "$0";
        private String totalNew = "0";
        private String totalUsed = "0";
        private String totalCollectible = "0";
        private String totalRefurbished = "0";

        public OfferSummary(com.amazon.xml.AWSECommerceService.OfferSummary os){
            if (os == null){
                return;
            }
            totalNew = os.getTotalNew();
            totalUsed = os.getTotalUsed();
            totalCollectible = os.getTotalCollectible();
            totalRefurbished = os.getTotalRefurbished();
            if (os.getLowestNewPrice() != null){
                lowestNewPriceAmmount = os.getLowestNewPrice().getAmount();
                lowestNewPriceCurrencyCode = os.getLowestNewPrice().getCurrencyCode();
                lowestNewPriceFormatted = os.getLowestNewPrice().getFormattedPrice();
            }
            if (os.getLowestUsedPrice() != null){
                lowestUsedPriceAmmount = os.getLowestUsedPrice().getAmount();
                lowestUsedPriceCurrencyCode = os.getLowestUsedPrice().getCurrencyCode();
                lowestUsedPriceFormatted = os.getLowestUsedPrice().getFormattedPrice();
            }
            if (os.getLowestCollectiblePrice() != null){
                lowestCollectiblePriceAmmount = os.getLowestCollectiblePrice().getAmount();
                lowestCollectiblePriceCurrencyCode = os.getLowestCollectiblePrice().getCurrencyCode();
                lowestCollectiblePriceFormatted = os.getLowestCollectiblePrice().getFormattedPrice();
            }
            if (os.getLowestRefurbishedPrice() != null){
                lowestRefurbishedPriceAmmount = os.getLowestRefurbishedPrice().getAmount();
                lowestRefurbishedPriceCurrencyCode = os.getLowestRefurbishedPrice().getCurrencyCode();
                lowestRefurbishedPriceFormatted = os.getLowestRefurbishedPrice().getFormattedPrice();
            }
        }

        public BigInteger getLowestCollectiblePriceAmmount() {
            return lowestCollectiblePriceAmmount;
        }

        public void setLowestCollectiblePriceAmmount(
                BigInteger lowestCollectiblePriceAmmount) {
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

        public BigInteger getLowestNewPriceAmmount() {
            return lowestNewPriceAmmount;
        }

        public void setLowestNewPriceAmmount(BigInteger lowestNewPriceAmmount) {
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

        public BigInteger getLowestRefurbishedPriceAmmount() {
            return lowestRefurbishedPriceAmmount;
        }

        public void setLowestRefurbishedPriceAmmount(
                BigInteger lowestRefurbishedPriceAmmount) {
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

        public BigInteger getLowestUsedPriceAmmount() {
            return lowestUsedPriceAmmount;
        }

        public void setLowestUsedPriceAmmount(BigInteger lowestUsedPriceAmmount) {
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


    public String getAsin() {
        return asin;
    }


    public void setAsin(String asin) {
        this.asin = asin;
    }


    public String getDetailPage() {
        return detailPage;
    }


    public void setDetailPage(String detailPage) {
        this.detailPage = detailPage;
    }


    public OfferSummary getOfferSummary() {
        return offerSummary;
    }


    public void setOfferSummary(OfferSummary offerSummary) {
        this.offerSummary = offerSummary;
    }


    public String getSalesRank() {
        return salesRank;
    }


    public void setSalesRank(String salesRank) {
        this.salesRank = salesRank;
    }


    public ItemAttributes getItemAttributes() {
        return itemAttributes;
    }


    public void setItemAttributes(ItemAttributes itemAttributes) {
        this.itemAttributes = itemAttributes;
    }


    public Image getLargeImage() {
        return largeImage;
    }


    public void setLargeImage(Image largeImage) {
        this.largeImage = largeImage;
    }


    public Image getMediumImage() {
        return mediumImage;
    }


    public void setMediumImage(Image mediumImage) {
        this.mediumImage = mediumImage;
    }


    public Image getSmallImage() {
        return smallImage;
    }


    public void setSmallImage(Image smallImage) {
        this.smallImage = smallImage;
    }

}
