lexer grammar FateLexer;

@header
{
   package tonkadur.fate.v1.parser;
}

fragment SEP: [ \t\r\n]+;

WS: SEP;

L_PAREN: WS* '(';
R_PAREN: WS* ')';

ADD_KW: L_PAREN 'add' WS*;
ADD_TO_ENUM_TYPE_KW: L_PAREN 'add_to_enum_type' WS*;
ADD_VARIABLE_ATTRIBUTE_KW: L_PAREN 'add_variable_attribute' WS*;
AND_KW: L_PAREN ('and'|'/\\') WS*;
ASSERT_KW: L_PAREN 'assert' WS*;
CAST_KW: L_PAREN 'cast' WS*;
CLEAR_KW: L_PAREN 'clear' WS*;
COND_KW: L_PAREN 'cond' WS*;
COUNT_KW: L_PAREN 'count' WS*;
DECLARE_ALIAS_TYPE_KW: L_PAREN 'declare_subtype' WS*;
DECLARE_DICT_TYPE_KW: L_PAREN 'declare_dict_type' WS*;
DECLARE_ENUM_TYPE_KW: L_PAREN 'declare_enum_type' WS*;
DECLARE_EVENT_TYPE_KW: L_PAREN 'declare_event_type' WS*;
DECLARE_TEXT_EFFECT: L_PAREN 'declare_text_effect' WS*;
DECLARE_TEXT_EFFECT_KW: L_PAREN 'declare_text_effect' WS*;
DECLARE_VARIABLE_KW: L_PAREN 'declare_variable' WS*;
DEFINE_MACRO_KW: L_PAREN 'define_macro' WS*;
DEFINE_SEQUENCE_KW: L_PAREN 'define_sequence' WS*;
DIVIDE_KW: L_PAREN ('divide'|'/') WS*;
ENABLE_TEXT_PARAMETER_KW: L_PAREN 'enable_text_parameter' WS*;
EQUALS_KW: L_PAREN ('equals'|'='|'==') WS*;
EVENT_KW: L_PAREN 'event' WS*;
FALSE_KW: L_PAREN 'false)';
FATE_VERSION_KW: L_PAREN 'fate_version' WS*;
GREATER_EQUAL_THAN_KW: L_PAREN ('greater_equal_than'|'>=') WS*;
GREATER_THAN_KW: L_PAREN ('greater_than'|'>') WS*;
IF_ELSE_KW: L_PAREN 'if_else' WS*;
IF_KW: L_PAREN 'if' WS*;
IMPLIES_KW: L_PAREN ('implies'|'=>') WS*;
IS_MEMBER_KW: L_PAREN 'is_member' WS*;
LOWER_EQUAL_THAN_KW: L_PAREN ('lower_equal_than'|'=<'|'<=') WS*;
LOWER_THAN_KW: L_PAREN ('lower_than'|'<') WS*;
MACRO_KW: L_PAREN 'macro' WS*;
MINUS_KW: L_PAREN ('minus'|'-') WS*;
NEWLINE_KW: L_PAREN 'newline' WS*;
NOT_KW: L_PAREN ('not'|'~'|'!') WS*;
ONE_IN_KW: L_PAREN 'one_in' WS*;
OR_KW: L_PAREN ('or'|'\\/') WS*;
PARAMETER_KW: L_PAREN 'parameter' WS*;
PLAYER_CHOICE_KW: L_PAREN 'player_choice' WS*;
PLUS_KW: L_PAREN ('plus'|'+') WS*;
POWER_KW: L_PAREN ('power'|'^'|'**') WS*;
RANDOM_KW: L_PAREN ('random'|'rand') WS*;
REMOVE_ALL_KW: L_PAREN 'remove_all' WS*;
REMOVE_ONE_KW: L_PAREN 'remove_one' WS*;
REQUIRE_KW: L_PAREN 'require' WS*;
SEQUENCE_KW: L_PAREN 'sequence' WS*;
SET_EXPRESSION_KW: L_PAREN 'set_expression' WS*;
SET_KW: L_PAREN 'set' WS*;
TIMES_KW: L_PAREN ('times'|'*') WS*;
TRUE_KW: L_PAREN 'true)';
VARIABLE_KW: L_PAREN 'variable' WS*;

WORD: (~([ \t\r\n()])|'\\)'|'\\(')+;

COMMENT: WS* ';' .*? '\n' -> channel(HIDDEN);
