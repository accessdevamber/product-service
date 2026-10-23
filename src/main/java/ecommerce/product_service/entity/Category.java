
package ecommerce.product_service.entity;

import ecommerce.product_service.util.UuidV7Generator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "categories", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Category {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 120)
    private String slug;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)//FetchType.LAZY Avoids automatically fetching the parent
    @JoinColumn(
            name = "parent_id",
            foreignKey = @ForeignKey(name = "fk_categories_parent")
    )
    private Category parent;

    @Column(name = "active", nullable = false)
    private boolean active = true;

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

//@ManyToOne This means:
//- Many child categories can have the same parent.
//- Each child category has at most one parent.
//For example, Mobiles and Laptops can both have Electronics as their parent.
//The @ManyToOne annotation tells Hibernate that parent is a relationship to another entity,
// not a normal field that should be stored directly.


//B. fetch = FetchType.LAZY
//@ManyToOne(fetch = FetchType.LAZY)
//
//
//JPA has two common fetching strategies:
//LAZY	EAGER
//Related entity loaded when needed	Related entity loaded as part of fetching the owner
//Generally better for avoiding unnecessary loads	Can cause unnecessary database queries
//Requires attention to transaction boundaries	Related entity is already available
//Suppose you load the Mobiles category:
//Category mobile = categoryRepository.findById(id).orElseThrow();
//
//System.out.println(mobile.getName());
//
//
//With LAZY fetching, Hibernate does not normally need to fetch Electronics just to return the name Mobiles.
//Now:
//System.out.println(mobile.getParent().getName());
//
//
//Hibernate may execute another query to fetch Electronics.
//Conceptually:
//SELECT * FROM public.categories WHERE id = '...'; -- Mobiles
//
//SELECT * FROM public.categories WHERE id = '...'; -- Electronics
//
//
//The second query is triggered when the parent data is accessed, unless the parent is already loaded in the persistence context.
//Important: Accessing a lazy relationship outside an active Hibernate session can cause LazyInitializationException.
// Later, we'll handle this properly through transactional service methods and DTO mapping.


//C. @JoinColumn(name = "parent_id")
//@JoinColumn(name = "parent_id")
//
//
//This specifies the foreign-key column in the owning table.
//So although the Java field is:
//private Category parent;
//
//
//the actual PostgreSQL column remains:
//parent_id UUID
//
//
//Yes, you are absolutely correct: parent_id is still a UUID column in PostgreSQL.
//JPA knows that the Category entity has:
//@Id
//private UUID id;
//
//
//Therefore, it knows that the parent_id column contains a UUID referencing another category.
//When you write:
//Category electronics = ...;
//
//Category mobiles = new Category();
//mobiles.setName("Mobiles");
//mobiles.setParent(electronics);
//
//
//Hibernate extracts the identifier from electronics and stores that UUID in mobiles.parent_id.
//It does not store the entire Java Category object inside the database column.


//D. What happened to referencedColumnName?
//Your older example:
//@JoinColumn(
//        name = "course_id",
//        referencedColumnName = "courseId"
//)
//
//
//The meanings are:
//- name — the foreign-key column in the current table.
//- referencedColumnName — the column in the referenced table.
//When you omit referencedColumnName, JPA defaults to referencing the target entity's primary-key column.
//Our code:
//@JoinColumn(name = "parent_id")
//
//
//is effectively equivalent to:
//@JoinColumn(
//        name = "parent_id",
//        referencedColumnName = "id"
//)
//
//
//because Category.id is the primary key mapped to the database column id.
//Nothing changed in JPA. We're simply relying on its default behavior.
//If your older Course entity used:
//@Id
//@Column(name = "courseId")
//private Long courseId;
//
//
//then referencedColumnName = "courseId" explicitly identified that database column.
//For Product Service, we can add referencedColumnName = "id" if you prefer explicitness, but it isn't necessary.


//2. What does ON DELETE RESTRICT mean?
//Our SQL:
//CONSTRAINT fk_categories_parent
//    FOREIGN KEY (parent_id)
//    REFERENCES public.categories(id)
//    ON DELETE RESTRICT
//
//
//It means PostgreSQL will prevent deletion of a parent category while any child category references it.
//For example:
//DELETE FROM public.categories
//WHERE name = 'Electronics';
//
//
//If Mobiles and Laptops still reference Electronics, PostgreSQL rejects the deletion with a foreign-key violation.
//Compare the common options:
//Option	Behavior when parent is deleted
//RESTRICT	Reject deletion if children reference it
//CASCADE	Automatically delete referencing rows
//SET NULL	Set the referencing FK columns to NULL
//NO ACTION	Reject invalid references, but can defer checking if the FK constraint is deferrable
//RESTRICT and NO ACTION are often effectively the same for ordinary non-deferrable foreign keys, but they differ in whether the check can be deferred.
//Why RESTRICT for our e-commerce project?
//We don't want deleting Electronics to automatically delete Mobiles, Laptops, and possibly associated product data.
//We'll manage category deletion carefully in the service layer.


//3. Category CHECK constraints
//A. Prevent a category from being its own parent
//CONSTRAINT chk_categories_not_self_parent
//    CHECK (parent_id IS NULL OR parent_id <> id)
//
//
//Let's break it down.
//parent_id IS NULL allows a root category, such as Electronics, to have no parent.
//parent_id <> id means the parent UUID must be different from the category's own UUID. The <> operator means not equal.
//id	parent_id	Condition	Allowed?
//A	NULL	parent_id IS NULL	Yes
//A	B	parent_id <> id	Yes
//A	A	Both conditions false	No
//Why use OR? Because either situation is valid: a root category has no parent, or a child category references another category.
//One nuance: PostgreSQL CHECK constraints pass when the expression evaluates to TRUE or UNKNOWN. The explicit IS NULL makes the intention clear even though the NULL case would also pass a simpler parent_id <> id check.
//This check only prevents direct self-reference. It does not prevent indirect cycles such as A → B → A.