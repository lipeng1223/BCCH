package com.bc.orm;

import java.io.Serializable;
import java.util.Date;
import java.util.StringTokenizer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name="audit")
public class Audit implements Serializable {

    public Audit(){}
    
    public Audit(Long tableId, String tableName){
        this.tableId = tableId;
        this.tableName = tableName;
    }
    
    private Long id;

    private Long tableId; // id of the table being modified
    private String tableName; // name of the table being modified
    private Long parentTableId; // order, receiving, or manifest, set when an item gets modified
    private String auditMessage;

    private Date auditTime;
    private String auditAction;
    private String columnName1;
    private String columnName2;
    private String columnName3;
    private String columnName4;
    private String columnName5;
    private String columnName6;
    private String columnName7;
    private String columnName8;
    private String columnName9;
    private String columnName10;
    private String columnName11;
    private String columnName12;
    private String columnName13;
    private String columnName14;
    private String columnName15;
    private String columnName16;
    private String columnName17;
    private String columnName18;
    private String columnName19;
    private String columnName20;
    private String previousValue1;
    private String previousValue2;
    private String previousValue3;
    private String previousValue4;
    private String previousValue5;
    private String previousValue6;
    private String previousValue7;
    private String previousValue8;
    private String previousValue9;
    private String previousValue10;
    private String previousValue11;
    private String previousValue12;
    private String previousValue13;
    private String previousValue14;
    private String previousValue15;
    private String previousValue16;
    private String previousValue17;
    private String previousValue18;
    private String previousValue19;
    private String previousValue20;
    private String currentValue1;
    private String currentValue2;
    private String currentValue3;
    private String currentValue4;
    private String currentValue5;
    private String currentValue6;
    private String currentValue7;
    private String currentValue8;
    private String currentValue9;
    private String currentValue10;
    private String currentValue11;
    private String currentValue12;
    private String currentValue13;
    private String currentValue14;
    private String currentValue15;
    private String currentValue16;
    private String currentValue17;
    private String currentValue18;
    private String currentValue19;
    private String currentValue20;
    private User user;
    private String username;
    
    // transient
    private Long userId;
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id", nullable=false)    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuditAction() {
        return auditAction;
    }

    public void setAuditAction(String auditAction) {
        this.auditAction = auditAction;
    }

    @Transient
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPreviousValue1() {
        return previousValue1;
    }

    public void setPreviousValue1(String previousValue1) {
        this.previousValue1 = previousValue1;
    }

    public String getPreviousValue2() {
        return previousValue2;
    }

    public void setPreviousValue2(String previousValue2) {
        this.previousValue2 = previousValue2;
    }

    public String getPreviousValue3() {
        return previousValue3;
    }

    public void setPreviousValue3(String previousValue3) {
        this.previousValue3 = previousValue3;
    }

    public String getPreviousValue4() {
        return previousValue4;
    }

    public void setPreviousValue4(String previousValue4) {
        this.previousValue4 = previousValue4;
    }

    public String getPreviousValue5() {
        return previousValue5;
    }

    public void setPreviousValue5(String previousValue5) {
        this.previousValue5 = previousValue5;
    }

    public String getPreviousValue6() {
        return previousValue6;
    }

    public void setPreviousValue6(String previousValue6) {
        this.previousValue6 = previousValue6;
    }

    public String getPreviousValue7() {
        return previousValue7;
    }

    public void setPreviousValue7(String previousValue7) {
        this.previousValue7 = previousValue7;
    }

    public String getPreviousValue8() {
        return previousValue8;
    }

    public void setPreviousValue8(String previousValue8) {
        this.previousValue8 = previousValue8;
    }

    public String getPreviousValue9() {
        return previousValue9;
    }

    public void setPreviousValue9(String previousValue9) {
        this.previousValue9 = previousValue9;
    }

    public String getPreviousValue10() {
        return previousValue10;
    }

    public void setPreviousValue10(String previousValue10) {
        this.previousValue10 = previousValue10;
    }

    public String getPreviousValue11() {
        return previousValue11;
    }

    public void setPreviousValue11(String previousValue11) {
        this.previousValue11 = previousValue11;
    }

    public String getPreviousValue12() {
        return previousValue12;
    }

    public void setPreviousValue12(String previousValue12) {
        this.previousValue12 = previousValue12;
    }

    public String getPreviousValue13() {
        return previousValue13;
    }

    public void setPreviousValue13(String previousValue13) {
        this.previousValue13 = previousValue13;
    }

    public String getPreviousValue14() {
        return previousValue14;
    }

    public void setPreviousValue14(String previousValue14) {
        this.previousValue14 = previousValue14;
    }

    public String getPreviousValue15() {
        return previousValue15;
    }

    public void setPreviousValue15(String previousValue15) {
        this.previousValue15 = previousValue15;
    }

    public String getPreviousValue16() {
        return previousValue16;
    }

    public void setPreviousValue16(String previousValue16) {
        this.previousValue16 = previousValue16;
    }

    public String getPreviousValue17() {
        return previousValue17;
    }

    public void setPreviousValue17(String previousValue17) {
        this.previousValue17 = previousValue17;
    }

    public String getPreviousValue18() {
        return previousValue18;
    }

    public void setPreviousValue18(String previousValue18) {
        this.previousValue18 = previousValue18;
    }

    public String getPreviousValue19() {
        return previousValue19;
    }

    public void setPreviousValue19(String previousValue19) {
        this.previousValue19 = previousValue19;
    }

    public String getPreviousValue20() {
        return previousValue20;
    }

    public void setPreviousValue20(String previousValue20) {
        this.previousValue20 = previousValue20;
    }

    public String getCurrentValue1() {
        return currentValue1;
    }

    public void setCurrentValue1(String currentValue1) {
        this.currentValue1 = currentValue1;
    }

    public String getCurrentValue2() {
        return currentValue2;
    }

    public void setCurrentValue2(String currentValue2) {
        this.currentValue2 = currentValue2;
    }

    public String getCurrentValue3() {
        return currentValue3;
    }

    public void setCurrentValue3(String currentValue3) {
        this.currentValue3 = currentValue3;
    }

    public String getCurrentValue4() {
        return currentValue4;
    }

    public void setCurrentValue4(String currentValue4) {
        this.currentValue4 = currentValue4;
    }

    public String getCurrentValue5() {
        return currentValue5;
    }

    public void setCurrentValue5(String currentValue5) {
        this.currentValue5 = currentValue5;
    }

    public String getCurrentValue6() {
        return currentValue6;
    }

    public void setCurrentValue6(String currentValue6) {
        this.currentValue6 = currentValue6;
    }

    public String getCurrentValue7() {
        return currentValue7;
    }

    public void setCurrentValue7(String currentValue7) {
        this.currentValue7 = currentValue7;
    }

    public String getCurrentValue8() {
        return currentValue8;
    }

    public void setCurrentValue8(String currentValue8) {
        this.currentValue8 = currentValue8;
    }

    public String getCurrentValue9() {
        return currentValue9;
    }

    public void setCurrentValue9(String currentValue9) {
        this.currentValue9 = currentValue9;
    }

    public String getCurrentValue10() {
        return currentValue10;
    }

    public void setCurrentValue10(String currentValue10) {
        this.currentValue10 = currentValue10;
    }

    public String getCurrentValue11() {
        return currentValue11;
    }

    public void setCurrentValue11(String currentValue11) {
        this.currentValue11 = currentValue11;
    }

    public String getCurrentValue12() {
        return currentValue12;
    }

    public void setCurrentValue12(String currentValue12) {
        this.currentValue12 = currentValue12;
    }

    public String getCurrentValue13() {
        return currentValue13;
    }

    public void setCurrentValue13(String currentValue13) {
        this.currentValue13 = currentValue13;
    }

    public String getCurrentValue14() {
        return currentValue14;
    }

    public void setCurrentValue14(String currentValue14) {
        this.currentValue14 = currentValue14;
    }

    public String getCurrentValue15() {
        return currentValue15;
    }

    public void setCurrentValue15(String currentValue15) {
        this.currentValue15 = currentValue15;
    }

    public String getCurrentValue16() {
        return currentValue16;
    }

    public void setCurrentValue16(String currentValue16) {
        this.currentValue16 = currentValue16;
    }

    public String getCurrentValue17() {
        return currentValue17;
    }

    public void setCurrentValue17(String currentValue17) {
        this.currentValue17 = currentValue17;
    }

    public String getCurrentValue18() {
        return currentValue18;
    }

    public void setCurrentValue18(String currentValue18) {
        this.currentValue18 = currentValue18;
    }

    public String getCurrentValue19() {
        return currentValue19;
    }

    public void setCurrentValue19(String currentValue19) {
        this.currentValue19 = currentValue19;
    }

    public String getCurrentValue20() {
        return currentValue20;
    }

    public void setCurrentValue20(String currentValue20) {
        this.currentValue20 = currentValue20;
    }
    
    @Transient
    public void setColumnName(int i, String value) {
        switch (i) {
        case 1:
            setColumnName1(value);
            break;
        case 2:
            setColumnName2(value);
            break;
        case 3:
            setColumnName3(value);
            break;
        case 4:
            setColumnName4(value);
            break;
        case 5:
            setColumnName5(value);
            break;
        case 6:
            setColumnName6(value);
            break;
        case 7:
            setColumnName7(value);
            break;
        case 8:
            setColumnName8(value);
            break;
        case 9:
            setColumnName9(value);
            break;
        case 10:
            setColumnName10(value);
            break;
        case 11:
            setColumnName11(value);
            break;
        case 12:
            setColumnName12(value);
            break;
        case 13:
            setColumnName13(value);
            break;
        case 14:
            setColumnName14(value);
            break;
        case 15:
            setColumnName15(value);
            break;
        case 16:
            setColumnName16(value);
            break;
        case 17:
            setColumnName17(value);
            break;
        case 18:
            setColumnName18(value);
            break;
        case 19:
            setColumnName19(value);
            break;
        case 20:
            setColumnName20(value);
            break;
        default:
            break;
        }
    }
    
    @Transient
    public void setCurrentValue(int i, String value){
        switch (i) {
        case 1:
            setCurrentValue1(value);
            break;
        case 2:
            setCurrentValue2(value);
            break;
        case 3:
            setCurrentValue3(value);
            break;
        case 4:
            setCurrentValue4(value);
            break;
        case 5:
            setCurrentValue5(value);
            break;
        case 6:
            setCurrentValue6(value);
            break;
        case 7:
            setCurrentValue7(value);
            break;
        case 8:
            setCurrentValue8(value);
            break;
        case 9:
            setCurrentValue9(value);
            break;
        case 10:
            setCurrentValue10(value);
            break;
        case 11:
            setCurrentValue11(value);
            break;
        case 12:
            setCurrentValue12(value);
            break;
        case 13:
            setCurrentValue13(value);
            break;
        case 14:
            setCurrentValue14(value);
            break;
        case 15:
            setCurrentValue15(value);
            break;
        case 16:
            setCurrentValue16(value);
            break;
        case 17:
            setCurrentValue17(value);
            break;
        case 18:
            setCurrentValue18(value);
            break;
        case 19:
            setCurrentValue19(value);
            break;
        case 20:
            setCurrentValue20(value);
            break;
        default:
            break;
        }
    }

    @Transient
    public void setPreviousValue(int i, String value){
        switch (i) {
        case 1:
            setPreviousValue1(value);
            break;
        case 2:
            setPreviousValue2(value);
            break;
        case 3:
            setPreviousValue3(value);
            break;
        case 4:
            setPreviousValue4(value);
            break;
        case 5:
            setPreviousValue5(value);
            break;
        case 6:
            setPreviousValue6(value);
            break;
        case 7:
            setPreviousValue7(value);
            break;
        case 8:
            setPreviousValue8(value);
            break;
        case 9:
            setPreviousValue9(value);
            break;
        case 10:
            setPreviousValue10(value);
            break;
        case 11:
            setPreviousValue11(value);
            break;
        case 12:
            setPreviousValue12(value);
            break;
        case 13:
            setPreviousValue13(value);
            break;
        case 14:
            setPreviousValue14(value);
            break;
        case 15:
            setPreviousValue15(value);
            break;
        case 16:
            setPreviousValue16(value);
            break;
        case 17:
            setPreviousValue17(value);
            break;
        case 18:
            setPreviousValue18(value);
            break;
        case 19:
            setPreviousValue19(value);
            break;
        case 20:
            setPreviousValue20(value);
            break;
        default:
            break;
        }
    }

    @Transient
    public String debugString(){
        StringBuilder sb = new StringBuilder("id: ");
        sb.append(id);
        sb.append("\nauditTime: ");
        sb.append(auditTime.toString());
        sb.append("\nauditAction: ");
        sb.append(auditAction);
        if (previousValue1 != null){
            sb.append("\ncolumnName1: ");
            sb.append(columnName1);
            sb.append("\npreviousValue2: ");
            sb.append(previousValue1);
            sb.append("\ncurrentValue2 ");
            sb.append(currentValue1);
        }
        if (previousValue2 != null){
            sb.append("\ncolumnName2: ");
            sb.append(columnName2);
            sb.append("\npreviousValue2: ");
            sb.append(previousValue2);
            sb.append("\ncurrentValue2 ");
            sb.append(currentValue2);
        }
        if (previousValue3 != null){
            sb.append("\ncolumnName3: ");
            sb.append(columnName3);
            sb.append("\npreviousValue3: ");
            sb.append(previousValue3);
            sb.append("\ncurrentValue3 ");
            sb.append(currentValue3);
        }
        if (previousValue4 != null){
            sb.append("\ncolumnName4: ");
            sb.append(columnName4);
            sb.append("\npreviousValue4: ");
            sb.append(previousValue4);
            sb.append("\ncurrentValue4 ");
            sb.append(currentValue4);
        }
        if (previousValue5 != null){
            sb.append("\ncolumnName5: ");
            sb.append(columnName5);
            sb.append("\npreviousValue5: ");
            sb.append(previousValue5);
            sb.append("\ncurrentValue5 ");
            sb.append(currentValue5);
        }
        if (previousValue6 != null){
            sb.append("\ncolumnName6: ");
            sb.append(columnName6);
            sb.append("\npreviousValue6: ");
            sb.append(previousValue6);
            sb.append("\ncurrentValue6 ");
            sb.append(currentValue6);
        }
        if (previousValue7 != null){
            sb.append("\ncolumnName7: ");
            sb.append(columnName7);
            sb.append("\npreviousValue7: ");
            sb.append(previousValue7);
            sb.append("\ncurrentValue7 ");
            sb.append(currentValue7);
        }
        if (previousValue8 != null){
            sb.append("\ncolumnName8: ");
            sb.append(columnName8);
            sb.append("\npreviousValue8: ");
            sb.append(previousValue8);
            sb.append("\ncurrentValue8 ");
            sb.append(currentValue8);
        }
        if (previousValue9 != null){
            sb.append("\ncolumnName9: ");
            sb.append(columnName9);
            sb.append("\npreviousValue9: ");
            sb.append(previousValue9);
            sb.append("\ncurrentValue9 ");
            sb.append(currentValue9);
        }
        if (previousValue10 != null){
            sb.append("\ncolumnName10: ");
            sb.append(columnName10);
            sb.append("\npreviousValue10: ");
            sb.append(previousValue10);
            sb.append("\ncurrentValue10 ");
            sb.append(currentValue10);
        }
        if (previousValue11 != null){
            sb.append("\ncolumnName11: ");
            sb.append(columnName11);
            sb.append("\npreviousValue11: ");
            sb.append(previousValue11);
            sb.append("\ncurrentValue11 ");
            sb.append(currentValue11);
        }
        if (previousValue12 != null){
            sb.append("\ncolumnName12: ");
            sb.append(columnName12);
            sb.append("\npreviousValue12: ");
            sb.append(previousValue12);
            sb.append("\ncurrentValue12 ");
            sb.append(currentValue12);
        }
        if (previousValue13 != null){
            sb.append("\ncolumnName13: ");
            sb.append(columnName13);
            sb.append("\npreviousValue13: ");
            sb.append(previousValue13);
            sb.append("\ncurrentValue13 ");
            sb.append(currentValue13);
        }
        if (previousValue14 != null){
            sb.append("\ncolumnName14: ");
            sb.append(columnName14);
            sb.append("\npreviousValue14: ");
            sb.append(previousValue14);
            sb.append("\ncurrentValue14 ");
            sb.append(currentValue14);
        }
        if (previousValue15 != null){
            sb.append("\ncolumnName15: ");
            sb.append(columnName15);
            sb.append("\npreviousValue15: ");
            sb.append(previousValue15);
            sb.append("\ncurrentValue15 ");
            sb.append(currentValue15);
        }
        if (previousValue16 != null){
            sb.append("\ncolumnName16: ");
            sb.append(columnName16);
            sb.append("\npreviousValue16: ");
            sb.append(previousValue16);
            sb.append("\ncurrentValue16 ");
            sb.append(currentValue16);
        }
        if (previousValue17 != null){
            sb.append("\ncolumnName17: ");
            sb.append(columnName17);
            sb.append("\npreviousValue17: ");
            sb.append(previousValue17);
            sb.append("\ncurrentValue17 ");
            sb.append(currentValue17);
        }
        if (previousValue18 != null){
            sb.append("\ncolumnName18: ");
            sb.append(columnName18);
            sb.append("\npreviousValue18: ");
            sb.append(previousValue18);
            sb.append("\ncurrentValue18 ");
            sb.append(currentValue18);
        }
        if (previousValue19 != null){
            sb.append("\ncolumnName19: ");
            sb.append(columnName19);
            sb.append("\npreviousValue19: ");
            sb.append(previousValue19);
            sb.append("\ncurrentValue19 ");
            sb.append(currentValue19);
        }
        if (previousValue20 != null){
            sb.append("\ncolumnName20: ");
            sb.append(columnName20);
            sb.append("\npreviousValue20: ");
            sb.append(previousValue20);
            sb.append("\ncurrentValue20 ");
            sb.append(currentValue20);
        }
        sb.append("\nuser id: ");
        sb.append(userId);
        sb.append("\nusername: ");
        sb.append(username);
        return sb.toString();
    }
    
    @Transient
    public Integer getChangeCount(){
        if (previousValue1 == null) return 1;
        if (previousValue2 == null) return 2;
        if (previousValue3 == null) return 3;
        if (previousValue4 == null) return 4;
        if (previousValue5 == null) return 5;
        if (previousValue6 == null) return 6;
        if (previousValue7 == null) return 7;
        if (previousValue8 == null) return 8;
        if (previousValue9 == null) return 9;
        if (previousValue10 == null) return 10;
        if (previousValue11 == null) return 11;
        if (previousValue12 == null) return 12;
        if (previousValue13 == null) return 13;
        if (previousValue14 == null) return 14;
        if (previousValue15 == null) return 15;
        if (previousValue16 == null) return 16;
        if (previousValue17 == null) return 17;
        if (previousValue18 == null) return 18;
        if (previousValue19 == null) return 19;
        return 20;
    }
    
    @Transient
    public String getColumnName(Integer i){
        switch (i) {
            case 1: return getColumnName1();
            case 2: return getColumnName2();
            case 3: return getColumnName3();
            case 4: return getColumnName4();
            case 5: return getColumnName5();
            case 6: return getColumnName6();
            case 7: return getColumnName7();
            case 8: return getColumnName8();
            case 9: return getColumnName9();
            case 10: return getColumnName10();
            case 11: return getColumnName11();
            case 12: return getColumnName12();
            case 13: return getColumnName13();
            case 14: return getColumnName14();
            case 15: return getColumnName15();
            case 16: return getColumnName16();
            case 17: return getColumnName17();
            case 18: return getColumnName18();
            case 19: return getColumnName19();
            case 20: return getColumnName20();
        }
        return "";
    }

    @Transient
    public String getCurrentValue(Integer i){
        switch (i) {
            case 1: return getCurrentValue1();
            case 2: return getCurrentValue2();
            case 3: return getCurrentValue3();
            case 4: return getCurrentValue4();
            case 5: return getCurrentValue5();
            case 6: return getCurrentValue6();
            case 7: return getCurrentValue7();
            case 8: return getCurrentValue8();
            case 9: return getCurrentValue9();
            case 10: return getCurrentValue10();
            case 11: return getCurrentValue11();
            case 12: return getCurrentValue12();
            case 13: return getCurrentValue13();
            case 14: return getCurrentValue14();
            case 15: return getCurrentValue15();
            case 16: return getCurrentValue16();
            case 17: return getCurrentValue17();
            case 18: return getCurrentValue18();
            case 19: return getCurrentValue19();
            case 20: return getCurrentValue20();
        }
        return "";
    }
    
    @Transient
    public String getPreviousValue(Integer i){
        switch (i) {
            case 1: return getPreviousValue1();
            case 2: return getPreviousValue2();
            case 3: return getPreviousValue3();
            case 4: return getPreviousValue4();
            case 5: return getPreviousValue5();
            case 6: return getPreviousValue6();
            case 7: return getPreviousValue7();
            case 8: return getPreviousValue8();
            case 9: return getPreviousValue9();
            case 10: return getPreviousValue10();
            case 11: return getPreviousValue11();
            case 12: return getPreviousValue12();
            case 13: return getPreviousValue13();
            case 14: return getPreviousValue14();
            case 15: return getPreviousValue15();
            case 16: return getPreviousValue16();
            case 17: return getPreviousValue17();
            case 18: return getPreviousValue18();
            case 19: return getPreviousValue19();
            case 20: return getPreviousValue20();
        }
        return "";
    }
    
    public String getColumnName1() {
        return columnName1;
    }

    public void setColumnName1(String columnName1) {
        this.columnName1 = columnName1;
    }

    public String getColumnName2() {
        return columnName2;
    }

    public void setColumnName2(String columnName2) {
        this.columnName2 = columnName2;
    }

    public String getColumnName3() {
        return columnName3;
    }

    public void setColumnName3(String columnName3) {
        this.columnName3 = columnName3;
    }

    public String getColumnName4() {
        return columnName4;
    }

    public void setColumnName4(String columnName4) {
        this.columnName4 = columnName4;
    }

    public String getColumnName5() {
        return columnName5;
    }

    public void setColumnName5(String columnName5) {
        this.columnName5 = columnName5;
    }

    public String getColumnName6() {
        return columnName6;
    }

    public void setColumnName6(String columnName6) {
        this.columnName6 = columnName6;
    }

    public String getColumnName7() {
        return columnName7;
    }

    public void setColumnName7(String columnName7) {
        this.columnName7 = columnName7;
    }

    public String getColumnName8() {
        return columnName8;
    }

    public void setColumnName8(String columnName8) {
        this.columnName8 = columnName8;
    }

    public String getColumnName9() {
        return columnName9;
    }

    public void setColumnName9(String columnName9) {
        this.columnName9 = columnName9;
    }

    public String getColumnName10() {
        return columnName10;
    }

    public void setColumnName10(String columnName10) {
        this.columnName10 = columnName10;
    }

    public String getColumnName11() {
        return columnName11;
    }

    public void setColumnName11(String columnName11) {
        this.columnName11 = columnName11;
    }

    public String getColumnName12() {
        return columnName12;
    }

    public void setColumnName12(String columnName12) {
        this.columnName12 = columnName12;
    }

    public String getColumnName13() {
        return columnName13;
    }

    public void setColumnName13(String columnName13) {
        this.columnName13 = columnName13;
    }

    public String getColumnName14() {
        return columnName14;
    }

    public void setColumnName14(String columnName14) {
        this.columnName14 = columnName14;
    }

    public String getColumnName15() {
        return columnName15;
    }

    public void setColumnName15(String columnName15) {
        this.columnName15 = columnName15;
    }

    public String getColumnName16() {
        return columnName16;
    }

    public void setColumnName16(String columnName16) {
        this.columnName16 = columnName16;
    }

    public String getColumnName17() {
        return columnName17;
    }

    public void setColumnName17(String columnName17) {
        this.columnName17 = columnName17;
    }

    public String getColumnName18() {
        return columnName18;
    }

    public void setColumnName18(String columnName18) {
        this.columnName18 = columnName18;
    }

    public String getColumnName19() {
        return columnName19;
    }

    public void setColumnName19(String columnName19) {
        this.columnName19 = columnName19;
    }

    public String getColumnName20() {
        return columnName20;
    }

    public void setColumnName20(String columnName20) {
        this.columnName20 = columnName20;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getParentTableId() {
        if (parentTableId != null)
            return parentTableId;
        return -1L;
    }

    public void setParentTableId(Long parentTableId) {
        this.parentTableId = parentTableId;
    }

    public String getAuditMessage() {
        return auditMessage;
    }

    public void setAuditMessage(String auditMessage) {
        this.auditMessage = auditMessage;
    }
    
    @Transient
    public String getIsbnFromAuditMessage(){
        if (auditMessage != null && auditMessage.startsWith("isbn")){
            StringTokenizer st = new StringTokenizer(auditMessage);
            if (st.countTokens() == 4) {
                st.nextToken();
                return st.nextToken();
            }
        }
        return "";
    }
    
    @Transient
    public String getCondFromAuditMessage(){
        if (auditMessage != null && auditMessage.startsWith("isbn")){
            StringTokenizer st = new StringTokenizer(auditMessage);
            if (st.countTokens() == 4) {
                st.nextToken();
                st.nextToken();
                st.nextToken();
                return st.nextToken();
            }
        }
        return "";
    }
    
    @Transient
    public String getPreviousBin(){
        if ("bin".equals(columnName1)) return previousValue1;
        else if ("bin".equals(columnName2)) return previousValue2;
        else if ("bin".equals(columnName3)) return previousValue3;
        else if ("bin".equals(columnName4)) return previousValue4;
        else if ("bin".equals(columnName5)) return previousValue5;
        else if ("bin".equals(columnName6)) return previousValue6;
        else if ("bin".equals(columnName7)) return previousValue7;
        else if ("bin".equals(columnName8)) return previousValue8;
        else if ("bin".equals(columnName9)) return previousValue9;
        else if ("bin".equals(columnName10)) return previousValue10;
        else if ("bin".equals(columnName11)) return previousValue11;
        else if ("bin".equals(columnName12)) return previousValue12;
        else if ("bin".equals(columnName13)) return previousValue13;
        else if ("bin".equals(columnName14)) return previousValue14;
        else if ("bin".equals(columnName15)) return previousValue15;
        else if ("bin".equals(columnName16)) return previousValue16;
        else if ("bin".equals(columnName17)) return previousValue17;
        else if ("bin".equals(columnName18)) return previousValue18;
        else if ("bin".equals(columnName19)) return previousValue19;
        else if ("bin".equals(columnName20)) return previousValue20;
        return "";
    }

    @Transient
    public String getCurrentBin(){
        if ("bin".equals(columnName1)) return currentValue1;
        else if ("bin".equals(columnName2)) return currentValue2;
        else if ("bin".equals(columnName3)) return currentValue3;
        else if ("bin".equals(columnName4)) return currentValue4;
        else if ("bin".equals(columnName5)) return currentValue5;
        else if ("bin".equals(columnName6)) return currentValue6;
        else if ("bin".equals(columnName7)) return currentValue7;
        else if ("bin".equals(columnName8)) return currentValue8;
        else if ("bin".equals(columnName9)) return currentValue9;
        else if ("bin".equals(columnName10)) return currentValue10;
        else if ("bin".equals(columnName11)) return currentValue11;
        else if ("bin".equals(columnName12)) return currentValue12;
        else if ("bin".equals(columnName13)) return currentValue13;
        else if ("bin".equals(columnName14)) return currentValue14;
        else if ("bin".equals(columnName15)) return currentValue15;
        else if ("bin".equals(columnName16)) return currentValue16;
        else if ("bin".equals(columnName17)) return currentValue17;
        else if ("bin".equals(columnName18)) return currentValue18;
        else if ("bin".equals(columnName19)) return currentValue19;
        else if ("bin".equals(columnName20)) return currentValue20;
        return "";
    }
}
