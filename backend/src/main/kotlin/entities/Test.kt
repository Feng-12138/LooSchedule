package entities

import javax.persistence.*

@Entity
@Table(name = "Test")
class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Int = 0

    @Column(name = "name", nullable = false)
    var name: String = ""
}

//@Entity(tableName = "users")
//data class Test(
//
//
//    @PrimaryKey val uid: Int,
//    @ColumnInfo(name = "id") val id: Int = 0,
//    @ColumnInfo(name = "name") val name: String = ""
//)
