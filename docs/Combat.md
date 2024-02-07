## Combat information

### Combat flow

- A player sends an attack, which creates an event in the event-manager. 

- When the event is completed, it sends a message to the combat-manager, which checks if 
any battle is already ongoing in the destination base. If it is, it joins the units in the attackers' side.
Otherwise, it starts a battle, with these units on the attackers' side,
and it requests the current units owned by the defending base and the support armies 
from the base-manager.

- A battle is composed of a list of rounds. During each round, combat-manager calculates
the battle development (what units are lost etc.).

- After the battle has started, each time a new attack unit joins the battle, the base-manager
immediately communicates this information to the combat-manager. However,
the list of support armies and own units in the defending base is manually fetched by the
combat-manager in the beginning of each round. This happens because sending a support
army does not mean a battle will be triggered, however sending an attacking army
always triggers a battle.

- When one of the sides has no remaining units, the battle ends. When this happens the information
is sent to the base-manager, which calculates the amount of resources that were looted and
creates a return event to the attackers' origin base.

- A battle report is created for the battle, which is updated each round and can be retrieved by the players
involved. Each player will only be able to retrieve the information for the rounds
they participated.

### Communication Flow

**Send Support Army:** base-manager --> event-manager -> base-manager

**Cancel Support Army (out of battle):** base-manager --> event-manager --> base-manager

**Cancel Support Army (in battle):** combat-manager --> event-manager --> base-manager

**Send Attack Army:** base-manager --> event-manager --> combat-manager

**Cancel Attack Army:** combat-manager --> event-manager --> base-manager

### Combat logic

There are two phases during a battle: Active Defenses Phase and Engagement Phase.

#### Active Defenses Phase

It's the first phase of the battle and happens while the base defenses have health points.

Attacker's damage is calculated by adding the damage from all units in the front line and multiplying by a scaling factor.

Each unit also has a specific damage factor to the base defenses. This because there are certain unit types that are able 
to deal more damage to defenses than others. For example, artillery will have a bigger damage factor than a Main Battle Tank
and a Main Battle Tank will have a bigger damage factor than infantry.

The unit's type damage is calculated by multiplying the following values:

- The amount of units from that type
- The unit's attack value
- The unit's damage factor
- The unit's accuracy

The scaling factor is a value between 0.75 and 1.25 and is retrieved from a Gaussian distribution.
This damage is then deduced from the base defense's health points.

Defender's damage is calculated for each unit group (i.e. ground, armored and air).

Each defense type only deals damage to the corresponding unit groups (e.g. ground defenses only take damage to ground units etc).
The damage is calculated by multiplying the base defense damage by a scaling factor.

Each unit type takes a percentage of damage corresponding to its percentage compared to the limit amounts of units in the front line
(e.g. if the front line has a limit amount of 800 units for ground units and the limit for infantry is 500, the infantry will take 62,5% of the damage,
even if there are only 10 infantry units). This is calculated for each unit type.
The amount of units lost is calculated by dividing the total damage to that unit type by the that unit's health points.
The "left over" damage is then distributed randomly to one of the other unit types, but only within the same group. So damage done, for example, by the Armored Defenses will not impact air units etc.

During this phase any existing units from the defender's side will not take part in the battle. Only the base defenses take part in the defender side.

#### Engagement Phase

During the engagement phase, the base defenses health points reach 0 and the units from the defender side enter the battle.
The base defenses no longer take part in the battle.

For this part of the battle the logic is much more complex. 

The damage is first calculated for each unit type. Each unit type will face each other first, dealing damage to each other.
This is calculated the same way as before. If there is "left over" damage, it will be distributed first to units of the same group 
and eventually to units of other groups if there is still damage left.