parser grammar FateParser;

options
{
   tokenVocab = FateLexer;
}

@header
{
   package tonkadur.fate.v1.parser;

   import tonkadur.fate.v1.lang.World;
}

@members
{
   /* of the class */
}

/******************************************************************************/
/******************************************************************************/
/******************************************************************************/
fate_file [World world]:
   WS* FATE_VERSION_KW WORD L_PAREN WS*
   (
      (first_level_fate_instr|general_fate_instr)
      {
      }
      WS*
   )*
   EOF
   {
   }
;

general_fate_sequence:
   (WS* general_fate_instr WS*)*
   {
   }
;

first_level_fate_instr:
   DEFINE_SEQUENCE_KW WORD WS+ general_fate_sequence R_PAREN
   {
   }

   | DECLARE_VARIABLE_KW range=WORD WS+ type=WORD WS+ name=WORD R_PAREN
   {
   }

   | DECLARE_TEXT_EFFECT_KW range=WORD R_PAREN
   {
   }

   | ADD_VARIABLE_ATTRIBUTE_KW WORD WS+ WORD R_PAREN
   {
   }

   | DECLARE_ALIAS_TYPE_KW parent=WORD WS+ name=WORD R_PAREN
   {
   }

   | DECLARE_DICT_TYPE_KW name=WORD typed_param_list R_PAREN
   {
   }

   | DECLARE_ENUM_TYPE_KW name=WORD R_PAREN
   {
   }

   | DECLARE_EVENT_TYPE_KW name=WORD WS+ word_list R_PAREN
   {
   }

   | ADD_TO_ENUM_TYPE_KW entry=WORD WS+ name=WORD R_PAREN
   {
   }

   | REQUIRE_KW WORD R_PAREN
   {
   }

   | DEFINE_MACRO_KW
         L_PAREN WS+ typed_param_list R_PAREN
         general_fate_sequence
      R_PAREN
   {
   }
;

general_fate_instr:
   L_PAREN general_fate_sequence R_PAREN
   {
   }

   | CLEAR_KW value_reference R_PAREN
   {
   }

   | SET_KW value WS+ value_reference R_PAREN
   {
   }

   | SET_EXPRESSION_KW value WS+ value_reference R_PAREN
   {
   }

   | EVENT_KW WORD WS+ value_list R_PAREN
   {
   }

   | MACRO_KW WORD WS+ value_list R_PAREN
   {
   }

   | SEQUENCE_KW WORD R_PAREN
   {
   }

   | ASSERT_KW value R_PAREN
   {
   }

   | IF_KW value WS+ general_fate_instr R_PAREN
   {
   }

   | IF_ELSE_KW value WS+ general_fate_instr WS+ general_fate_instr R_PAREN
   {
   }

   | COND_KW instr_cond_list R_PAREN
   {
   }

   | PLAYER_CHOICE_KW player_choice* R_PAREN
   {
   }

   | text+
   {
   }
;

instr_cond_list:
   (L_PAREN value WS+ general_fate_instr R_PAREN)+
   {
   }
;

player_choice:
   L_PAREN L_PAREN text+ R_PAREN WS+ general_fate_instr R_PAREN
   {
   }

   | IF_KW value WS+ player_choice R_PAREN
   {
   }

   | IF_ELSE_KW value WS+ player_choice WS+ player_choice R_PAREN
   {
   }

   | COND_KW player_choice_cond_list R_PAREN
   {
   }
;

player_choice_cond_list:
   (L_PAREN value WS+ player_choice R_PAREN)+
   {
   }
;

text:
   sentence
   {
   }

   | ENABLE_TEXT_PARAMETER_KW WORD WS+ text+ R_PAREN
   {
   }

   | NEWLINE_KW
   {
   }

   | non_text_value
   {
   }
;

sentence
   @init
   {
      final StringBuilder string_builder = new StringBuilder();
   }
   :

   first_word=WORD
   (
      WS next_word=WORD
      {
         string_builder.append(" ");
         string_builder.append(($next_word.text));
      }
   )+
   {
      string_builder.insert(0, ($first_word.text));
   }
;

word_list:
   WORD?
   {
   }

   | WORD (WS+ WORD)*
   {
   }
;

typed_param_list:
   (L_PAREN WORD WS+ WORD R_PAREN)*
   {
   }
;
/******************************************************************************/
/**** VALUES ******************************************************************/
/******************************************************************************/
boolean_expression:
   AND_KW value_list R_PAREN
   {
   }

   | OR_KW value_list R_PAREN
   {
   }

   | ONE_IN_KW value_list R_PAREN
   {
   }

   | NOT_KW value R_PAREN
   {
   }

   | IMPLIES_KW value WS+ value R_PAREN
   {
   }

   | LOWER_THAN_KW value WS+ value R_PAREN
   {
   }

   | LOWER_EQUAL_THAN_KW value WS+ value R_PAREN
   {
   }

   | EQUALS_KW value WS+ value R_PAREN
   {
   }

   | GREATER_EQUAL_THAN_KW value WS+ value R_PAREN
   {
   }

   | GREATER_THAN_KW value WS+ value R_PAREN
   {
   }

   | IS_MEMBER_KW value WS+ value_reference R_PAREN
   {
   }
;

math_expression:
   PLUS_KW value_list R_PAREN
   {
   }

   | MINUS_KW value_list R_PAREN
   {
   }

   | TIMES_KW value_list R_PAREN
   {
   }

   | DIVIDE_KW value WS+ value R_PAREN
   {
   }

   | POWER_KW value WS+ value R_PAREN
   {
   }

   | RANDOM_KW value WS+ value R_PAREN
   {
   }

   | COUNT_KW value WS+ value_reference R_PAREN
   {
   }
;

bag_expression:
   | ADD_KW value WS+ value_reference R_PAREN
   {
   }

   | REMOVE_ONE_KW value WS+ value_reference R_PAREN
   {
   }

   | REMOVE_ALL_KW value WS+ value_reference R_PAREN
   {
   }
;

value:
   WORD
   {
   }

   | L_PAREN WS* sentence R_PAREN
   {
   }

   | non_text_value
   {
   }
;

non_text_value:
   IF_ELSE_KW value WS+ value WS+ value R_PAREN
   {
   }

   | COND_KW value_cond_list R_PAREN
   {
   }

   | boolean_expression
   {
   }

   | math_expression
   {
   }

   | CAST_KW WORD WORD value R_PAREN
   {
   }

   | value_reference
   {
   }

   | SET_KW value WS+ value_reference R_PAREN
   {
   }
;

value_reference:
   VARIABLE_KW WORD R_PAREN
   {
   }

   | PARAMETER_KW WORD R_PAREN
   {
   }
;

value_cond_list:
   (L_PAREN value WS+ value R_PAREN)+
   {
   }
;

value_list:
   value* (WS+ value)*
   {
   }
;


