import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

object Day24 {
    enum class Damage {
        radiation, fire, bludgeoning, cold, slashing
    }

    private class Group(
        val id: String,
        val army: Int,
        val initialUnits: Int,
        val hitsPoints: Int,
        val weak: Set<Damage>,
        val immune: Set<Damage>,
        val damage: Damage,
        val amount: Int,
        val initiative: Int,
    ) : Comparable<Group> {
        var units = initialUnits
        var boost = 0
        val power: Int
            get() = units * (amount + boost)

        fun reset(withBoost: Int = 0): Group {
            units = initialUnits
            boost = withBoost
            return this
        }

        override fun compareTo(other: Group) = compareBy(Group::power, Group::initiative).compare(other, this)

        fun damage(other: Group) = when (damage) {
            in other.immune -> 0
            in other.weak -> 2 * power
            else -> power
        }
    }

    private fun parse(input: List<String>): List<MutableList<Group>> {
        val armies = List(2) { mutableListOf<Group>() }
        var name = ""
        var army = -1
        var id = 1
        fun react(line: String, kind: String) = Regex("""$kind to ([^;)]+)""").find(line)?.groupValues?.get(1)
            ?.let { it.split(", ").map { Damage.valueOf(it) }.toSet() } ?: emptySet()
        for (line in input) {
            if (line.endsWith(":")) {
                name = line.dropLast(1)
                army++
                id = 1
            } else if (!line.isBlank()) {
                val ints = line.ints()
                val weak = react(line, "weak")
                val immune = react(line, "immune")
                val damage = Regex("""(\w+) damage""").find(line)!!.groupValues[1].let { Damage.valueOf(it) }
                armies[army] += Group(
                    "$name group ${id++}",
                    army,
                    ints[0],
                    ints[1],
                    weak,
                    immune,
                    damage,
                    ints[2],
                    ints[3]
                )
            }
        }
        return armies
    }

    fun one(input: List<String>): Int {
        val (immune, infection) = parse(input)
        fights(immune, infection)
        return (immune + infection).sumOf { it.units }
    }

    private fun fights(immune: MutableList<Group>, infection: MutableList<Group>) {
        do {
            val groups = (immune + infection).sorted()
            val attacks = mutableMapOf<String, String>()
            for (g in groups) {
                val target = groups.filter { it.army != g.army && it.id !in attacks.values }
                    .map { it to g.damage(it) }.maxByOrNull { it.second }
                if (target != null && target.second > 0) {
                    attacks[g.id] = target.first.id
                }
            }
            var kills = 0
            for (g in groups.sortedByDescending { it.initiative }) {
                if (g.units <= 0) continue
                val t = groups.find { it.id == attacks[g.id] }
                if (t == null || t.units <= 0) continue
                val kill = g.damage(t) / t.hitsPoints
                t.units -= kill
                kills += kill
            }
            immune.removeAll { it.units <= 0 }
            infection.removeAll { it.units <= 0 }
        } while (immune.isNotEmpty() && infection.isNotEmpty() && kills > 0)
    }

    fun two(input: List<String>): Int {
        val (origImmune, origInfection) = parse(input)
        var minBoost = 0
        var maxBoost = 1000
        while (true) {
            val boost = (maxBoost + minBoost) / 2
            val immune = origImmune.map { it.reset(boost) }.toMutableList()
            val infection = origInfection.map { it.reset() }.toMutableList()
            fights(immune, infection)
            if (immune.isEmpty()) {
                minBoost = boost + 1
                maxBoost *= 2
            } else if (minBoost == maxBoost) {
                return immune.sumOf { it.units }
            } else if (infection.isEmpty()) {
                maxBoost = boost
            } else {
                minBoost = boost + 1
                maxBoost = boost + 2
            }
        }
    }
}

object Day24Test : FunSpec({
    val input = lines("Day24")

    val sample = """
        Immune System:
        17 units each with 5390 hit points (weak to radiation, bludgeoning) with an attack that does 4507 fire damage at initiative 2
        989 units each with 1274 hit points (immune to fire; weak to bludgeoning, slashing) with an attack that does 25 slashing damage at initiative 3

        Infection:
        801 units each with 4706 hit points (weak to radiation) with an attack that does 116 bludgeoning damage at initiative 1
        4485 units each with 2961 hit points (immune to radiation; weak to fire, cold) with an attack that does 12 slashing damage at initiative 4
    """.trimIndent().lines()

    test("one") {
        Day24.one(sample) shouldBe 5216
        Day24.one(input) shouldBe 15470
    }

    test("two") {
        Day24.two(sample) shouldBe 51
        Day24.two(input) shouldBe 5742
    }
})
