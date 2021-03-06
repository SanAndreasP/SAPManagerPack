/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.core.manpack.transformer;

import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static org.objectweb.asm.tree.AbstractInsnNode.*;

public final class InstructionComparator
{
    public static boolean fieldInsnEqual(FieldInsnNode insn1, FieldInsnNode insn2) {
        return insn1.owner.equals(insn2.owner) && insn1.name.equals(insn2.name) && insn1.desc.equals(insn2.desc);
    }

    public static InsnList getImportantList(InsnList list) {
        if( list.size() == 0 ) {
            return list;
        }

        HashMap<LabelNode, LabelNode> labels = new HashMap<>();

        for( AbstractInsnNode insn = list.getFirst(); insn != null; insn = insn.getNext() ) {
            if( insn instanceof LabelNode ) {
                labels.put((LabelNode) insn, (LabelNode) insn);
            }
        }

        InsnList importantNodeList = new InsnList();

        for( AbstractInsnNode insn = list.getFirst(); insn != null; insn = insn.getNext() ) {
            if( insn instanceof LabelNode || insn instanceof LineNumberNode ) {
                continue;
            }

            importantNodeList.add(insn.clone(labels));
        }
        return importantNodeList;
    }

    public static boolean iincInsnEqual(IincInsnNode node1, IincInsnNode node2) {
        return node1.var == node2.var && node1.incr == node2.incr;
    }

    public static boolean insnEqual(AbstractInsnNode node1, AbstractInsnNode node2) {
        if( node1.getType() != node2.getType() ) {
            return false;
        } else if( node1.getOpcode() != node2.getOpcode() ) {
            return false;
        }

        switch( node2.getType() ) {
            case VAR_INSN:
                return varInsnEqual((VarInsnNode) node1, (VarInsnNode) node2);
            case TYPE_INSN:
                return typeInsnEqual((TypeInsnNode) node1, (TypeInsnNode) node2);
            case FIELD_INSN:
                return fieldInsnEqual((FieldInsnNode) node1, (FieldInsnNode) node2);
            case METHOD_INSN:
                return methodInsnEqual((MethodInsnNode) node1, (MethodInsnNode) node2);
            case LDC_INSN:
                return ldcInsnEqual((LdcInsnNode) node1, (LdcInsnNode) node2);
            case IINC_INSN:
                return iincInsnEqual((IincInsnNode) node1, (IincInsnNode) node2);
            case INT_INSN:
                return intInsnEqual((IntInsnNode) node1, (IntInsnNode) node2);
            default:
                return true;
        }
    }

    public static List<Integer> insnListFind(InsnList haystack, InsnList needle) {
        LinkedList<Integer> list = new LinkedList<>();

        for( int start = 0; start <= haystack.size() - needle.size(); start++ ) {
            if( insnListMatches(haystack, needle, start) ) {
                list.add(start);
            }
        }

        return list;
    }

    public static List<AbstractInsnNode> insnListFindEnd(InsnList haystack, InsnList needle) {
        LinkedList<AbstractInsnNode> callNodes = new LinkedList<>();

        for( int callPoint : insnListFind(haystack, needle) ) {
            callNodes.add(haystack.get(callPoint + needle.size() - 1));
        }
        return callNodes;
    }

    public static List<InsnListSection> insnListFindL(InsnList haystack, InsnList needle) {
        HashSet<LabelNode> controlFlowLabels = new HashSet<>();

        for( AbstractInsnNode insn = haystack.getFirst(); insn != null; insn = insn.getNext() ) {
            switch( insn.getType() ) {
                case 8:
                case 15:
                    break;
                case 7:
                    JumpInsnNode jinsn = (JumpInsnNode) insn;
                    controlFlowLabels.add(jinsn.label);
                    break;
                case 11:
                    TableSwitchInsnNode tsinsn = (TableSwitchInsnNode) insn;
                    for( LabelNode label : tsinsn.labels ) {
                        controlFlowLabels.add(label);
                    }
                    break;
                case 12:
                    LookupSwitchInsnNode lsinsn = (LookupSwitchInsnNode) insn;
                    for( LabelNode label : lsinsn.labels ) {
                        controlFlowLabels.add(label);
                    }
                    break;
            }
        }

        LinkedList<InsnListSection> list = new LinkedList<>();

        nextsection:
        for( int start = 0; start <= haystack.size() - needle.size(); start++ ) {
            InsnListSection section = insnListMatchesL(haystack, needle, start, controlFlowLabels);
            if( section != null ) {
                for( InsnListSection asection : list ) {
                    if( asection.last == section.last ) {
                        continue nextsection;
                    }
                }

                list.add(section);
            }
        }

        return list;
    }

    public static List<AbstractInsnNode> insnListFindStart(InsnList haystack, InsnList needle) {
        LinkedList<AbstractInsnNode> callNodes = new LinkedList<>();

        for( int callPoint : insnListFind(haystack, needle) ) {
            callNodes.add(haystack.get(callPoint));
        }
        return callNodes;
    }

    public static boolean insnListMatches(InsnList haystack, InsnList needle, int start) {
        if( haystack.size() - start < needle.size() ) {
            return false;
        }

        for( int i = 0; i < needle.size(); i++ ) {
            if( !insnEqual(haystack.get(i + start), needle.get(i)) ) {
                return false;
            }
        }
        return true;
    }

    private static InsnListSection insnListMatchesL(InsnList haystack, InsnList needle, int start,
                                                    HashSet<LabelNode> controlFlowLabels) {
        int h = start, n = 0;

        for(; h < haystack.size() && n < needle.size(); h++ ) {
            AbstractInsnNode insn = haystack.get(h);

            if( insn.getType() == 15 ) {
                continue;
            }
            if( insn.getType() == 8 && !controlFlowLabels.contains(insn) ) {
                continue;
            }
            if( !insnEqual(haystack.get(h), needle.get(n)) ) {
                return null;
            }
            n++;
        }
        if( n != needle.size() ) {
            return null;
        }

        return new InsnListSection(haystack, start, h - 1);
    }

    public static boolean intInsnEqual(IntInsnNode node1, IntInsnNode node2) {
        return node1.operand == -1 || node2.operand == -1 || node1.operand == node2.operand;

    }

    public static boolean ldcInsnEqual(LdcInsnNode insn1, LdcInsnNode insn2) {
        return insn1.cst.equals("~") || insn2.cst.equals("~") || insn1.cst.equals(insn2.cst);

    }

    public static boolean methodInsnEqual(MethodInsnNode insn1, MethodInsnNode insn2) {
        return insn1.owner.equals(insn2.owner) && insn1.name.equals(insn2.name) && insn1.desc.equals(insn2.desc);
    }

    public static boolean typeInsnEqual(TypeInsnNode insn1, TypeInsnNode insn2) {
        return insn1.desc.equals("~") || insn2.desc.equals("~") || insn1.desc.equals(insn2.desc);

    }

    public static boolean varInsnEqual(VarInsnNode insn1, VarInsnNode insn2) {
        return insn1.var == -1 || insn2.var == -1 || insn1.var == insn2.var;

    }

    public static class InsnListSection
    {
        public AbstractInsnNode first;

        public AbstractInsnNode last;

        public InsnListSection(InsnList haystack, int start, int end) {
            this.first = haystack.get(start);
            this.last = haystack.get(end);
        }
    }
}
