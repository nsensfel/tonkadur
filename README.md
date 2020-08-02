# ![Tonkadur](https://tonkadur.of.tacticians.online/images/tonkadur_logo_black_as_path.svg)
Narrative scripting/programming language.

 * **Plain-text format.** Just write in your favorite editor and use your usual tools.
 * **Extensive description language (Fate).** You shouldn't feel restrained in what you can write.
 * **Strong typing.** Reduce the likeliness of errors in your descriptions.
 * **Very simple and small interpreted language (Wyrd).** Easily add support for Tonkadur to your engine.
 * **LISP inspired syntax.** No weird symbols everywhere. No annoying indentation restrictions.

Tonkadur provides a compiler from Fate to Wyrd, letting you freely describe
your stories using a feature rich language without having to worry about the
implications when it comes time to add support for it to your engine.

### Sample:
         (define_sequence in_your_room
            (ifelse
               (is_member visited_your_room progress)
               (text_effect narrator
                  You room is still a mess. You don't have time to clean things up,
                  though.
               )
               (text_effect narrator
                  You room is a mess. You recall having been through every drawer while
                  preparing your bag yesterday. While still unclear on how you are
                  supposed to pack all the necessary things for what promises to be at
                  least a year-long journey inside a small backpack, you cannot avoid
                  but wasting more time contemplating the piles of items that didn't
                  make the cut.
               )
            )
            (add visited_your_room progress)
            (player_choice
               (
                  ( Look for healing items )
                  (sequence look_for_healing_items)
               )
               (
                  ( No time! Let's go adventuring! )
                  (sequence leave_your_room)
               )
            )
         )

### Known alternatives
* [Inkle's Ink](https://www.inklestudios.com/ink/).
* [Yarn Spinner](https://yarnspinner.dev/).
* [DLG](https://github.com/iLambda/language-dlg). Nowhere near the popularity of
the other two alternatives, but its approach shares similarities with Tonkadur.
