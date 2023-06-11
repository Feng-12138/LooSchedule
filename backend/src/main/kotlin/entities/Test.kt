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
