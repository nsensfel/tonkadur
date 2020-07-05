parser grammar FateParser;

options
{
   tokenVocab = FateLexer;
}

@header
{
   package tonkadur.fate.v1.parser;

   import java.util.Map;
   import java.util.HashMap;

   import tonkadur.error.ErrorManager;

   import tonkadur.parser.Context;
   import tonkadur.parser.Origin;

   import tonkadur.fate.v1.error.IllegalReferenceNameException;
   import tonkadur.fate.v1.error.UnknownVariableScopeException;

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
   DEFINE_SEQUENCE_KW
      new_reference_name
      WS+
      first_node=general_fate_sequence
   R_PAREN
   {
   /*
      world.sequences().add
      (
         CONTEXT.get_origin_at
         (
            ($DEFINE_SEQUENCE_KW.getLine()),
            ($DEFINE_SEQUENCE_KW.getCharPositionInLine())
         ),
         ($new_reference_name.result),
         //(first_node.result)
      );
   */
   }

   | DECLARE_VARIABLE_KW scope=WORD WS+ type WS+ name=new_reference_name R_PAREN
   {
      final Origin start_origin, scope_origin, type_origin;
      final Variable new_variable;
      VariableScope variable_scope;

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

      variable_scope = VariableScope.value_of(($scope.text));

      if (variable_scope == null)
      {
         ErrorManager.handle
         (
            new UnknownVariableScopeException(scope_origin, ($scope.text))
         );

         variable_scope = VariableScope.ANY;
      }

      new_variable =
         new Variable
         (
            start_origin,
            variable_scope,
            ($type.result),
            ($name.result)
         );

      WORLD.variables().add(new_variable);
   }

   | DECLARE_TEXT_EFFECT_KW params=type_list new_reference_name R_PAREN
   {
      final Origin start_origin;
      final TextEffect new_text_effect;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DECLARE_TEXT_EFFECT_KW.getLine()),
            ($DECLARE_TEXT_EFFECT_KW.getCharPositionInLine())
         );

      new_text_effect =
         new TextEffect
         (
            start_origin,
            ($type_list.result),
            ($new_reference_name.result)
         );

      WORLD.text_effects().add(new_text_effect);
   }

   | REQUIRE_EXTENSION_KW WORD R_PAREN
   {
      WORLD.add_required_extension(($WORD.text));
   }

   | DECLARE_ALIAS_TYPE_KW parent=type WS+ new_reference_name R_PAREN
   {
      final Origin start_origin;
      final Type new_type;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DECLARE_ALIAS_TYPE_KW.getLine()),
            ($DECLARE_ALIAS_TYPE_KW.getCharPositionInLine())
         );

      new_type =
         new Type
         (
            start_origin,
            ($parent.result),
            ($new_reference_name.result)
         );

      WORLD.types().add(new_type);
   }

   | DECLARE_DICT_TYPE_KW new_reference_name WS* typed_entry_list R_PAREN
   {
      final Origin start_origin;
      final Type new_type;
      final Map<String, Type> field_types;

      field_types = new HashMap<String, Type>();

      for
      (
         final TypedEntryList.TypedEntry te:
            ($typed_entry_list.result).get_entries()
      )
      {
         field_types.put(te.get_name(), te.get_type());
      }

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DECLARE_DICT_TYPE_KW.getLine()),
            ($DECLARE_DICT_TYPE_KW.getCharPositionInLine())
         );

      new_type =
         new DictType
         (
            start_origin,
            field_types,
            ($new_reference_name.result)
         );

      WORLD.types().add(new_type);
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

   | DECLARE_EVENT_TYPE_KW new_reference_name WS+ type_list R_PAREN
   {
      final Origin start_origin;
      final Event new_event;

      start_origin =
         CONTEXT.get_origin_at
         (
            ($DECLARE_EVENT_TYPE_KW.getLine()),
            ($DECLARE_EVENT_TYPE_KW.getCharPositionInLine())
         );

      new_event =
         new Event
         (
            start_origin,
            ($type_list.result),
            ($new_reference_name.result)
         );

      WORLD.events().add(new_event);
   }

   | REQUIRE_KW WORD R_PAREN
   {
   }

   | DEFINE_MACRO_KW
         new_reference_name WS*
         L_PAREN WS+ typed_entry_list R_PAREN
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

type
returns [Type result]
:
   WORD
   {
      $result =
         WORLD.types().get
         (
            CONTEXT.get_origin_at
            (
               ($WORD.getLine()),
               ($WORD.getCharPositionInLine())
            ),
            ($WORD.text)
         );
   }
;
catch [final Throwable e]
{
   throw new ParseCancellationException(e);
}

type_list
returns [List<Type> result]
@init
{
   $result = new ArrayList<Type>();
}
:
   (
      type
      {
         $result.add(($type.result));
      }
   )?
   (WS+
      type
      {
         $result.add(($type.result));
      }
   )*
   {
   }
;

typed_entry_list
returns [TypedEntryList result]
@init
{
   $result = new TypedEntryList();
}
:
   (
      L_PAREN type WS+ new_reference_name R_PAREN
      {
         $result.add
         (
            CONTEXT.get_origin_at
            (
               ($L_PAREN.getLine()),
               ($L_PAREN.getCharPositionInLine())
            ),
            ($type.result),
            ($new_reference_name.result)
         );
      }
   )*
   {
   }
;
catch [final Throwable e]
{
   throw new ParseCancellationException(e);
}

new_reference_name
returns [String result]
:
   WORD
   {
      if (($WORD.text).indexOf('.') != -1)
      {
         ErrorManager.handle
         (
            new IllegalReferenceNameException
            (
               CONTEXT.get_origin_at
               (
                  ($WORD.getLine()),
                  ($WORD.getCharPositionInLine())
               ),
               ($WORD.text)
            )
         );
      }

      $result = ($WORD.text);
   }
;
catch [final Throwable e]
{
   throw new ParseCancellationException(e);
}

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


