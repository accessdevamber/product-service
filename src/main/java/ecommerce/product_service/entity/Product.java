
package ecommerce.product_service.entity;

import ecommerce.product_service.enums.ProductStatus;
import ecommerce.product_service.util.UuidV7Generator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "products", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_products_category")
    )
    private Category category;

    @Column(name = "sku", nullable = false, unique = true, length = 80)
    private String sku;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 220)
    private String slug;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "brand", length = 100)
    private String brand;

    @Column(
            name = "price",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal price;

    //In JPA/Hibernate, the length = 3 attribute in the @Column annotation defines the maximum length
    // of the column in the database, not an exact length constraint.
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(
            name = "currency",
            nullable = false,
            length = 3,
            columnDefinition = "char(3)"
    )
    //@Size(min = 3, max = 3, message = "Currency must be exactly 3 characters")
    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProductStatus status = ProductStatus.DRAFT;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "specifications",
            nullable = false,
            columnDefinition = "jsonb"
    )
    private Map<String, Object> specifications = new HashMap<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {

        if (id == null) {
            id = UuidV7Generator.generate();
        }

        Instant now = Instant.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}

//4. Product entity — category_id foreign key
//Our Flyway SQL:
//category_id UUID NOT NULL,
//
//CONSTRAINT fk_products_category
//    FOREIGN KEY (category_id)
//    REFERENCES public.categories(id)
//    ON DELETE RESTRICT
//
//
//Java:
//@ManyToOne(fetch = FetchType.LAZY, optional = false)
//@JoinColumn(
//        name = "category_id",
//        nullable = false,
//        foreignKey = @ForeignKey(name = "fk_products_category")
//)
//private Category category;
//
//
//The logic is almost identical to parent_id, except this time the relationship is between two different tables.
//categories
//id UUID (PK)
//name = Mobiles
//
//
//products
//id UUID (PK)
//category_id UUID (FK)
//name = Smartphone
//
//
//
//products.category_id → categories.id
//
//
//What is optional = false?
//@ManyToOne(fetch = FetchType.LAZY, optional = false)
//
//
//This tells JPA that the relationship is mandatory.
//A product must belong to a category.
//And:
//@JoinColumn(name = "category_id", nullable = false)
//
//
//declares that the database join column must not be nullable.
//In the Flyway SQL, we enforce the same rule:
//category_id UUID NOT NULL
//
//
//The difference:
//- optional = false expresses the mandatory relationship in JPA metadata.
//- nullable = false expresses the join-column nullability.
//- NOT NULL in Flyway is the actual database constraint.
//If someone bypasses Spring Boot and inserts SQL directly, PostgreSQL still rejects a product with no category.


//5. JSONB — default value and CHECK constraint
//Our SQL:
//specifications JSONB NOT NULL DEFAULT '{}'::jsonb
//
//
//This has four parts:
//Part	Meaning
//specifications	Column name
//JSONB	PostgreSQL binary JSON datatype
//NOT NULL	SQL NULL is not allowed
//DEFAULT '{}'::jsonb	Use an empty JSON object when the column is omitted
//What does '{}'::jsonb mean?
//'{}' represents an empty JSON object.
//::jsonb is PostgreSQL's type-cast syntax.
//Equivalent:
//CAST('{}' AS JSONB)
//
//
//Example:
//SELECT '{}'::jsonb;
//
//
//Result:
//{}
//
//
//This is different from an empty JSON array:
//SELECT '[]'::jsonb;
//
//
//Result:
//[]
//
//
//Why do we have a CHECK constraint?
//CONSTRAINT chk_products_specs_object
//    CHECK (jsonb_typeof(specifications) = 'object')
//
//
//Although JSONB accepts valid JSON, JSON itself can contain objects, arrays, strings, numbers, booleans and null.
//For example, these are all valid JSONB values:
//'{"ram":"8GB"}'::jsonb
//'["Java","SQL"]'::jsonb
//'"hello"'::jsonb
//'123'::jsonb
//'true'::jsonb
//
//
//But our product specifications should always be a JSON object containing key-value pairs.
//jsonb_typeof() checks the top-level JSON type.
//JSONB value	jsonb_typeof	Allowed?
//{"ram":"8GB"}	object	Yes
//{}	object	Yes
//["8GB","16GB"]	array	No
//"hello"	string	No
//123	number	No
//null (JSON null)	null	No
//This ensures that Product Service always receives a consistent top-level JSON structure.
//A Java mapping such as:
//@JdbcTypeCode(SqlTypes.JSON)
//@Column(
//        name = "specifications",
//        nullable = false,
//        columnDefinition = "jsonb"
//)
//private Map<String, Object> specifications = new HashMap<>();
//
//
//fits this design because a Map<String, Object> serializes naturally into a JSON object.
