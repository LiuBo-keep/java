package cn.hp.domain;

import javax.persistence.*;

/**
 * @author LiuBo
 * @date 2021/7/1
 * @Description 客户实体类
 *
 *  配置映射关系：
 *     1.实体类和表的映射关系
 *      @Entity: 声明实体类
 *      @Table: 配置实体类和表的映射关系
 *           name:配置数据库表的名称
 *
 *     2.实体类中属性和表中字段的映射关系
 *        @Id： 声明主键
 *        @GeneratedValue： 主键的生成策略
 *           GenerationType.IDENTITY： 自增
 *        @Column: 配置属性和字段的映射关系
 *          name： 数据库表中的字段名称
 */

@Entity
@Table(name = "cst_customer")
public class Customer {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cust_id")
    private Long custId;

    /**
     * 名称
     */
    @Column(name = "cust_name")
    private String custName;

    /**
     * 来源
     */
    @Column(name = "cust_source")
    private String custSource;

    /**
     * 级别
     */
    @Column(name = "cust_level")
    private String custLevel;

    /**
     * 所属行业
     */
    @Column(name = "cust_industry")
    private String custIndustry;

    /**
     * 电话
     */
    @Column(name = "cust_phone")
    private String custPhone;

    /**
     * 地址
     */
    @Column(name = "cust_address")
    private String custAddress;

    public Long getCustId() {
        return custId;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }



    public String getCustSource() {
        return custSource;
    }

    public void setCustSource(String custSource) {
        this.custSource = custSource;
    }

    public String getCustLevel() {
        return custLevel;
    }

    public void setCustLevel(String custLevel) {
        this.custLevel = custLevel;
    }

    public String getCustIndustry() {
        return custIndustry;
    }

    public void setCustIndustry(String custIndustry) {
        this.custIndustry = custIndustry;
    }

    public String getCustPhone() {
        return custPhone;
    }

    public void setCustPhone(String custPhone) {
        this.custPhone = custPhone;
    }

    public String getCustAddress() {
        return custAddress;
    }

    public void setCustAddress(String custAddress) {
        this.custAddress = custAddress;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "custId=" + custId +
                ", custName='" + custName + '\'' +
                ", custSource='" + custSource + '\'' +
                ", custLevel='" + custLevel + '\'' +
                ", custIndustry='" + custIndustry + '\'' +
                ", custPhone='" + custPhone + '\'' +
                ", custAddress='" + custAddress + '\'' +
                '}';
    }
}
