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