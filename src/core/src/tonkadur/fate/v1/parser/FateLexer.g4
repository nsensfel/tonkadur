lexer grammar FateLexer;

@header
{
   package tonkadur.fate.v1.parser;
}

fragment SEP: [ \t\r\n]+;

WS: SEP;

L_PAREN: '(';
R_PAREN: ')';


ADD_KW: L_PAREN 'add' SEP+;
AND_KW: L_PAREN ('and'|'/\\') SEP+;
ASSERT_KW: L_PAREN 'assert' SEP+;
AT_KW: L_PAREN 'at' SEP+;
CAST_KW: L_PAREN 'cast' SEP+;
CLEAR_KW: L_PAREN 'clear' SEP+;
COND_KW: L_PAREN 'cond' SEP+;
COUNT_KW: L_PAREN 'count' SEP+;
DECLARE_ALIAS_TYPE_KW: L_PAREN ((('declare'|'define'|'def')'_'('sub'('_'?))?'type')|'typedef') SEP+;
DECLARE_DICT_TYPE_KW: L_PAREN ('declare'|'define'|'def')'_dict_type' SEP+;
DECLARE_EVENT_TYPE_KW: L_PAREN ('declare'|'define'|'def')'_event_type' SEP+;
DECLARE_LIST_TYPE_KW: L_PAREN ('declare'|'define'|'def')'_list_type' SEP+;
DECLARE_REF_TYPE_KW: L_PAREN ('declare'|'define'|'def')'_ref_type' SEP+;
DECLARE_SET_TYPE_KW: L_PAREN ('declare'|'define'|'def')'_set_type' SEP+;
DECLARE_TEXT_EFFECT_KW: L_PAREN ('declare'|'define'|'def')'_text_effect' SEP+;
DECLARE_VARIABLE_KW: L_PAREN ('declare'|'define'|'def')'_var'('iable')? SEP+;
DEFINE_MACRO_KW: L_PAREN ('declare'|'define'|'def')'_macro' SEP+;
DEFINE_SEQUENCE_KW: L_PAREN ('declare'|'define'|'def')'_sequence' SEP+;
DIVIDE_KW: L_PAREN ('divide'|'/'|'div') SEP+;
ENABLE_TEXT_EFFECT_KW: L_PAREN 'text_effect' SEP+;
EQUALS_KW: L_PAREN ('equals'|'='|'=='|'eq') SEP+;
EVENT_KW: L_PAREN 'event' SEP+;
EXTENSION_FIRST_LEVEL_KW: L_PAREN '@';
EXTENSION_INSTRUCTION_KW: L_PAREN '#';
EXTENSION_VALUE_KW: L_PAREN '$';
FALSE_KW: L_PAREN 'false)';
FATE_VERSION_KW: L_PAREN 'fate_version' SEP+;
FIELD_KW: L_PAREN 'field' SEP+;
GREATER_EQUAL_THAN_KW: L_PAREN ('greater_equal_than'|'>='|'ge') SEP+;
GREATER_THAN_KW: L_PAREN ('greater_than'|'>'|'gt') SEP+;
IF_ELSE_KW: L_PAREN ('if_else'|'ifelse') SEP+;
IF_KW: L_PAREN 'if' SEP+;
IMPLIES_KW: L_PAREN ('implies'|'=>') SEP+;
INCLUDE_KW: L_PAREN 'include' SEP+;
IS_MEMBER_KW: L_PAREN ('is_member'|'contains') SEP+;
LOWER_EQUAL_THAN_KW: L_PAREN ('lower_equal_than'|'=<'|'<='|'le') SEP+;
LOWER_THAN_KW: L_PAREN ('lower_than'|'<'|'lt') SEP+;
IMACRO_KW: L_PAREN 'imacro' SEP+;
VMACRO_KW: L_PAREN 'vmacro' SEP+;
MINUS_KW: L_PAREN ('minus'|'-') SEP+;
NEWLINE_KW: L_PAREN 'newline)';
NOT_KW: L_PAREN ('not'|'~'|'!') SEP+;
ONE_IN_KW: L_PAREN 'one_in' SEP+;
OR_KW: L_PAREN ('or'|'\\/') SEP+;
PARAMETER_KW: L_PAREN ('param'|'parameter'|'par') SEP+;
PLAYER_CHOICE_KW: L_PAREN ('choice'|'user_choice'|'player_choice') SEP+;
PLUS_KW: L_PAREN ('plus'|'+') SEP+;
POWER_KW: L_PAREN ('power'|'^'|'**') SEP+;
RANDOM_KW: L_PAREN ('random'|'rand') SEP+;
REF_KW: L_PAREN 'ref' SEP+;
REMOVE_ALL_KW: L_PAREN 'remove_all' SEP+;
REMOVE_ONE_KW: L_PAREN 'remove_one' SEP+;
REQUIRE_EXTENSION_KW: L_PAREN 'require_extension' SEP+;
REQUIRE_KW: L_PAREN 'require' SEP+;
SEQUENCE_KW: L_PAREN 'sequence' SEP+;
SET_FIELDS_KW: L_PAREN 'set_fields' SEP+;
SET_KW: L_PAREN 'set' SEP+;
TIMES_KW: L_PAREN ('times'|'*') SEP+;
TRUE_KW: L_PAREN 'true)';
VAL_KW: L_PAREN ('val'|'value') SEP+;
VARIABLE_KW: L_PAREN ('variable'|'var') SEP+;

WORD: (~([ \t\r\n()])|'\\)'|'\\(')+;

COMMENT: WS* ';' .*? '\n' -> channel(HIDDEN);
