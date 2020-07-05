parser grammar FateParser;

options
{
   tokenVocab = FateLexer;
}

@header
{
   package tonkadur.fate.v1.parser;

   import tonkadur.error.Error;

   import tonkadur.parser.Context;
   import tonkadur.parser.Origin;

   import tonkadur.fate.v1.lang.*;
   import tonkadur.fate.v1.lang.meta.*;
}

@members
{
   Context CONTEXT;
   World WORLD;
}

/******************************************************************************/
/******************************************************************************/
/******************************************************************************/
fate_file [Context context, World world]
   @init
   {
      CONTEXT = context;
      WORLD = world;
   }
   :
   WS* FATE_VERSION_KW WORD R_PAREN WS*
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
   DEFINE_SEQUENCE_KW WORD WS+ first_node=general_fate_sequence R_PAREN
   {
   /*
      world.sequences().add
      (
         CONTEXT.get_origin_at
         (
            ($DEFINE_SEQUENCE_KW.getLine()),
            ($DEFINE_SEQUENCE_KW.getCharPositionInLine())
         ),
         ($WORD.text),
         //(first_node.result)
      );
   */
   }

   | DECLARE_VARIABLE_KW scope=WORD WS+ type=WORD WS+ name=WORD R_PAREN
   {
   /*
      final Origin start_origin, scope_origin, type_origin;
      final VariableScope variable_scope;
      final Type variable_type;
      final Variable new_variable;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DECLARE_VARIABLE_KW.getLine()),
            ($DECLARE_VARIABLE_KW.getCharPositionInLine())
         );

      scope_origin =
         CONTEXT.get_origin_at
         (
            ($scope.getLine()),
            ($scope.getCharPositionInLine())
         );

      type_origin =
         CONTEXT.get_origin_at
         (
            ($type.getLine()),
            ($type.getCharPositionInLine())
         );

      variable_scope = VariableScope.value_of(scope_origin, ($scope.text));
      variable_type = WORLD.types().get(($type.text));
      new_variable =
         new Variable
         (
            start_origin,
            variable_scope,
            variable_type,
            ($name.text)
         );

      WORLD.variables().add(new_variable);
   */
   }

   | DECLARE_TEXT_EFFECT_KW params=word_list name=WORD R_PAREN
   {
   }

   | REQUIRE_EXTENSION_KW name=WORD R_PAREN
   {
   }

   | DECLARE_ALIAS_TYPE_KW parent=WORD WS+ name=WORD R_PAREN
   {
      final Origin start_origin, parent_origin;
      final Type parent_type;
      final Type new_type;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DECLARE_ALIAS_TYPE_KW.getLine()),
            ($DECLARE_ALIAS_TYPE_KW.getCharPositionInLine())
         );

      parent_origin =
         CONTEXT.get_origin_at
         (
            ($parent.getLine()),
            ($parent.getCharPositionInLine())
         );

      parent_type = WORLD.types().get(parent_origin, ($parent.text));
      new_type = new Type(start_origin, parent_type, ($name.text));

      WORLD.types().add(new_type);
   }

   | DECLARE_DICT_TYPE_KW name=WORD typed_param_list R_PAREN
   {
   }

   | ADD_KW value WS+ value_reference R_PAREN
   {
   }

   | REMOVE_ONE_KW value WS+ value_reference R_PAREN
   {
   }

   | REMOVE_ALL_KW value WS+ value_reference R_PAREN
   {
   }

   | DECLARE_EVENT_TYPE_KW name=WORD WS+ word_list R_PAREN
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
catch [final Throwable e]
{
   throw new ParseCancellationException(e);
}

general_fate_instr:
   L_PAREN WS+ general_fate_sequence R_PAREN
   {
   }

   | CLEAR_KW value_reference R_PAREN
   {
   }

   | SET_KW value WS+ value_reference R_PAREN
   {
   }

   | SET_FIELD_KW WORD WS+ value WS+ value_reference R_PAREN
   {
   }

   | SET_EXPRESSION_KW value WS+ value_reference R_PAREN
   {
      /* that one isn't resolved until the value is referenced */
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
boolean_expression
returns [ValueNode result]:
   TRUE_KW
   {
      $result =
         Constant.build_boolean
         (
            CONTEXT.get_origin_at
            (
               ($TRUE_KW.getLine()),
               ($TRUE_KW.getCharPositionInLine())
            ),
            true
         );
   }

   | FALSE_KW
   {
      $result =
         Constant.build_boolean
         (
            CONTEXT.get_origin_at
            (
               ($FALSE_KW.getLine()),
               ($FALSE_KW.getCharPositionInLine())
            ),
            false
         );
   }

   | AND_KW value_list R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($AND_KW.getLine()),
               ($AND_KW.getCharPositionInLine())
            ),
            Operator.AND,
            ($value_list.result)
         );
   }

   | OR_KW value_list R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($OR_KW.getLine()),
               ($OR_KW.getCharPositionInLine())
            ),
            Operator.OR,
            ($value_list.result)
         );
   }

   | ONE_IN_KW value_list R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($ONE_IN_KW.getLine()),
               ($ONE_IN_KW.getCharPositionInLine())
            ),
            Operator.ONE_IN,
            ($value_list.result)
         );
   }

   | NOT_KW value_list R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($NOT_KW.getLine()),
               ($NOT_KW.getCharPositionInLine())
            ),
            Operator.NOT,
            ($value_list.result)
         );
   }

   | IMPLIES_KW value_list R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($IMPLIES_KW.getLine()),
               ($IMPLIES_KW.getCharPositionInLine())
            ),
            Operator.IMPLIES,
            ($value_list.result)
         );
   }

   | LOWER_THAN_KW value_list R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($LOWER_THAN_KW.getLine()),
               ($LOWER_THAN_KW.getCharPositionInLine())
            ),
            Operator.LOWER_THAN,
            ($value_list.result)
         );
   }

   | LOWER_EQUAL_THAN_KW value_list R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($LOWER_EQUAL_THAN_KW.getLine()),
               ($LOWER_EQUAL_THAN_KW.getCharPositionInLine())
            ),
            Operator.LOWER_EQUAL_THAN,
            ($value_list.result)
         );
   }

   | EQUALS_KW value_list R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($EQUALS_KW.getLine()),
               ($EQUALS_KW.getCharPositionInLine())
            ),
            Operator.EQUALS,
            ($value_list.result)
         );
   }

   | GREATER_EQUAL_THAN_KW value_list R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($GREATER_EQUAL_THAN_KW.getLine()),
               ($GREATER_EQUAL_THAN_KW.getCharPositionInLine())
            ),
            Operator.GREATER_EQUAL_THAN,
            ($value_list.result)
         );
   }

   | GREATER_THAN_KW value_list R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($GREATER_THAN_KW.getLine()),
               ($GREATER_THAN_KW.getCharPositionInLine())
            ),
            Operator.GREATER_THAN,
            ($value_list.result)
         );
   }

   | IS_MEMBER_KW value WS+ value_reference R_PAREN
   {
      /* TODO */
      $result = null;
   }
;
catch [final Throwable e]
{
   throw new ParseCancellationException(e);
}

math_expression
returns [ValueNode result]:
   PLUS_KW value_list R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($PLUS_KW.getLine()),
               ($PLUS_KW.getCharPositionInLine())
            ),
            Operator.PLUS,
            ($value_list.result)
         );
   }

   | MINUS_KW value_list R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($MINUS_KW.getLine()),
               ($MINUS_KW.getCharPositionInLine())
            ),
            Operator.MINUS,
            ($value_list.result)
         );
   }

   | TIMES_KW value_list R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($TIMES_KW.getLine()),
               ($TIMES_KW.getCharPositionInLine())
            ),
            Operator.TIMES,
            ($value_list.result)
         );
   }

   | DIVIDE_KW value_list R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($DIVIDE_KW.getLine()),
               ($DIVIDE_KW.getCharPositionInLine())
            ),
            Operator.DIVIDE,
            ($value_list.result)
         );
   }

   | POWER_KW value_list R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($POWER_KW.getLine()),
               ($POWER_KW.getCharPositionInLine())
            ),
            Operator.POWER,
            ($value_list.result)
         );
   }

   | RANDOM_KW value_list R_PAREN
   {
      $result =
         Operation.build
         (
            CONTEXT.get_origin_at
            (
               ($RANDOM_KW.getLine()),
               ($RANDOM_KW.getCharPositionInLine())
            ),
            Operator.RANDOM,
            ($value_list.result)
         );
   }

   | COUNT_KW value WS+ value_reference R_PAREN
   {
      /* TODO */
      $result= null;
   }
;
catch [final Throwable e]
{
   throw new ParseCancellationException(e);
}

value
returns [ValueNode result]
:
   WORD
   {
      $result =
         Constant.build
         (
            CONTEXT.get_origin_at
            (
               ($WORD.getLine()),
               ($WORD.getCharPositionInLine())
            ),
            ($WORD.text)
         );
   }

   | L_PAREN WS+ sentence R_PAREN
   {
      /* TODO */
      $result = null;
   }

   | non_text_value
   {
      $result = ($non_text_value.result);
   }
;

non_text_value
returns [ValueNode result]
:
   IF_ELSE_KW cond=value WS+ if_true=value WS+ if_false=value R_PAREN
   {
      $result =
         IfElseValue.build
         (
            CONTEXT.get_origin_at
            (
               ($IF_ELSE_KW.getLine()),
               ($IF_ELSE_KW.getCharPositionInLine())
            ),
            ($cond.result),
            ($if_true.result),
            ($if_false.result)
         );
   }

   | COND_KW value_cond_list R_PAREN
   {
      /* TODO */
      $result = null;
   }

   | boolean_expression
   {
      $result = ($boolean_expression.result);
   }

   | math_expression
   {
      $result = ($math_expression.result);
   }

   | CAST_KW WORD value R_PAREN
   {
      final Origin target_type_origin;
      final Type target_type;

      target_type_origin =
         CONTEXT.get_origin_at
         (
            ($WORD.getLine()),
            ($WORD.getCharPositionInLine())
         );

      target_type = WORLD.types().get(target_type_origin, ($WORD.text));

      $result =
         Cast.build
         (
            CONTEXT.get_origin_at
            (
               ($WORD.getLine()),
               ($WORD.getCharPositionInLine())
            ),
            target_type,
            ($value.result)
         );
   }

   | value_reference
   {
      /* TODO */
      $result = null;
   }
;
catch [final Throwable e]
{
   throw new ParseCancellationException(e);
}

value_reference:
   VARIABLE_KW WORD R_PAREN
   {
   }

   | PARAMETER_KW WORD R_PAREN
   {
   }

   | GET_KW value_reference R_PAREN
   {
   }
;

value_cond_list:
   (L_PAREN value WS+ value R_PAREN)+
   {
   }
;

value_list
returns [List<ValueNode> result]
@init
{
   $result = new ArrayList<ValueNode>();
}
:
   (
      value
      {
         ($result).add(($value.result));
      }
   )*
   (WS+
      value
      {
         ($result).add(($value.result));
      }
   )*
   {
   }
;


